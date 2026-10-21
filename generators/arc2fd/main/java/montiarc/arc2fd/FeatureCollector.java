/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd;

import com.google.common.base.Preconditions;
import arcbasis.context.WithinComponentResolver;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Collects the features of a component and, optionally, of its (transitive)
 * subcomponent instances, as well as the constraints over them.
 * <p>
 * Features of subcomponents are flattened into the root component. Their
 * feature diagram names join the instance path and the feature name with
 * {@link #SEPARATOR}, e.g., feature <code>b</code> of subcomponent instance
 * <code>s</code> becomes <code>s$b</code>. When flattening, names containing the
 * separator are reported as errors, hence flattened names are unique.
 * </p>
 * <p>
 * Features of parents are features of the child. Features of subcomponents
 * that are inherited from a parent are not collected.
 * </p>
 */
public class FeatureCollector {

  /**
   * Separator for flattened feature names. It is valid in feature diagram identifiers and practically
   * unused in MontiArc names.
   */
  public static final String SEPARATOR = "$";

  protected final boolean recursive;
  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  /**
   * @param recursive Whether to include the features and constraints of subcomponents
   */
  public FeatureCollector(boolean recursive,
                          @NotNull WithinComponentResolver withinComponentResolver,
                          @NotNull PresenceResolver presenceResolver) {
    this.recursive = recursive;
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  /**
   * Computes a mapping from feature names to be used in the feature diagram to feature names that can
   * be used by the SMT solver.
   *
   * @param root Component to analyze
   * @return Mapping from feature diagram names to SMT names, or <code>Optional.empty()</code> if a name
   * contains the {@link #SEPARATOR} (logged as error)
   */
  public Optional<Map<String, String>> collectFeatures(@NotNull IVariableArcComponentTypeSymbol root) {
    Preconditions.checkNotNull(root);

    String rootName = root.getTypeInfo().getName();
    Map<String, String> features = new LinkedHashMap<>();
    List<String> invalid = new ArrayList<>();

    traverse(root, (component, path) -> {
      if (!path.isEmpty() && path.getLast().contains(SEPARATOR))
        invalid.add(path.getLast());

      for (String feature : featuresOf(component)) {
        if (feature.contains(SEPARATOR))
          invalid.add(feature);
        features.put(qualify(path, feature, SEPARATOR), qualify(path, feature, "."));
      }
      return true;
    });

    // without flattening, names cannot collide
    if (!recursive)
      return Optional.of(features);

    invalid.forEach(name -> Log.error(Arc2FDError.INVALID_NAME.format(name, rootName, SEPARATOR)));

    return invalid.isEmpty() ? Optional.of(features) : Optional.empty();
  }

  /**
   * @param component Component to analyze
   * @return The names of the features declared by <code>component</code> and, transitively, its parents.
   * VariableArc binds inherited features to features of the same name in the child.
   */
  protected static Set<String> featuresOf(@NotNull IVariableArcComponentTypeSymbol component) {
    Set<String> features = new LinkedHashSet<>();
    collectFeaturesOf(component, features, new LinkedHashSet<>());
    return features;
  }

  private static void collectFeaturesOf(@NotNull IVariableArcComponentTypeSymbol component,
                                        @NotNull Set<String> features,
                                        @NotNull Set<ComponentTypeSymbol> visited) {
    if (!visited.add(component.getTypeInfo()))
      return;

    IVariableArcScope scope = (IVariableArcScope) component.getTypeInfo().getSpannedScope();
    scope.getLocalArcFeatureSymbols().forEach(feature -> features.add(feature.getName()));

    for (var parent : component.getTypeInfo().getSuperComponentsList()) {
      if (parent.getTypeInfo() instanceof IVariableArcComponentTypeSymbol parentSym)
        collectFeaturesOf(parentSym, features, visited);
    }
  }

  /**
   * Converts the constraints of the component into SMT assertions. These are
   * the constraints of the component, of its parents, and of its
   * subcomponents where these are present, with the parameters of
   * subcomponents bound to their arguments. Subcomponent constraints are
   * included regardless of {@link #recursive}, as they may restrict the
   * features of the component.
   *
   * @param root Component to analyze
   * @return Assertions in the context of {@link #getContext()}, or <code>Optional.empty()</code> if the type of
   * some constraint is obscure
   */
  public Optional<List<BoolExpr>> collectConstraints(@NotNull IVariableArcComponentTypeSymbol root) {
    Preconditions.checkNotNull(root);

    FormulaSolver solver = this.presenceResolver.solver();
    BoolExpr constraints = this.constraintsOf(solver, root.getTypeInfo(), new HashSet<>());
    if (solver.isObscure(constraints))
      return Optional.empty();
    return Optional.of(List.of(constraints));
  }

  /**
   * @return The context of the assertions of {@link #collectConstraints}
   */
  public Context getContext() {
    return this.presenceResolver.getContext();
  }

  /**
   * @param path The full names of the component types on the path from the root, to terminate on cycles
   */
  protected BoolExpr constraintsOf(@NotNull FormulaSolver solver, @NotNull ComponentTypeSymbol type, @NotNull Set<String> path) {
    if (!path.add(type.getFullName()))
      return solver.top();

    List<BoolExpr> operands = new ArrayList<>();
    operands.add(this.presenceResolver.presenceOf(type));
    for (SubcomponentSymbol subcomponent : this.withinComponentResolver.getAll(type, SubcomponentSymbol.class)) {
      if (subcomponent.isTypePresent()) {
        operands.add(solver.implies(this.presenceResolver.presenceOf(type, subcomponent),
          this.presenceResolver.lift(type, subcomponent,
            this.constraintsOf(solver, subcomponent.getType().getTypeInfo(), path))));
      }
    }
    path.remove(type.getFullName());
    return solver.and(operands);
  }

  /* ---------------------------------------------------------------------- */
  /* Component traversal                                                    */
  /* ---------------------------------------------------------------------- */

  /**
   * Callback for {@link #traverse}.
   */
  @FunctionalInterface
  protected interface ComponentVisitor {

    /**
     * @param component Visited component
     * @param path      Names of the subcomponent instances leading from the root to <code>component</code>,
     *                  empty for the root itself
     * @return <code>true</code> to continue the traversal, <code>false</code> to abort it
     */
    boolean visit(@NotNull IVariableArcComponentTypeSymbol component, @NotNull List<String> path);
  }

  /**
   * Visits <code>root</code> and, if {@link #recursive}, all its (transitive) subcomponent instances in
   * pre-order. Cycles in the component hierarchy are not followed.
   *
   * @param root    Component to start at
   * @param visitor Callback invoked for each visited component
   * @return <code>true</code>, iff no visit aborted the traversal
   */
  protected boolean traverse(@NotNull IVariableArcComponentTypeSymbol root, @NotNull ComponentVisitor visitor) {
    return traverse(root, List.of(), visitor, new LinkedHashSet<>());
  }

  private boolean traverse(@NotNull IVariableArcComponentTypeSymbol component,
                           @NotNull List<String> path,
                           @NotNull ComponentVisitor visitor,
                           @NotNull Set<ComponentTypeSymbol> visiting) {
    if (!visiting.add(component.getTypeInfo()))
      return true;

    if (!visitor.visit(component, path))
      return false;

    if (recursive) {
      for (var sub : component.getTypeInfo().getSubcomponents()) {
        if (sub.isTypePresent() && sub.getType().getTypeInfo() instanceof IVariableArcComponentTypeSymbol subSym) {
          List<String> subPath = new ArrayList<>(path);
          subPath.add(sub.getName());
          if (!traverse(subSym, subPath, visitor, visiting))
            return false;
        }
      }
    }

    visiting.remove(component.getTypeInfo());
    return true;
  }

  private static String qualify(@NotNull List<String> path, @NotNull String name, @NotNull String separator) {
    return path.isEmpty() ? name : String.join(separator, path) + separator + name;
  }
}
