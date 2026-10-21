/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Checks that the constraints of a component type imply the constraints of
 * each of its subcomponents wherever these are present. The constraints of a
 * subcomponent may refer to the features of its own subcomponents, which are
 * hidden from the component type; for these, it suffices that some values
 * satisfy the constraints. As every component type is checked, the
 * constraints of a component type and its super components describe its
 * valid configurations without those of its subcomponents. Subcomponents
 * whose type has no valid configuration are left to
 * {@link ConstraintSatisfied4Comp}.
 */
public class SubcomponentConstraintsImplied implements ArcBasisASTArcComponentTypeCoCo {

  protected static final String LOG_NAME = SubcomponentConstraintsImplied.class.getSimpleName();

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;

  /**
   * The results of {@link #checkNotImplied} by subcomponent, which
   * {@link SubcomponentsConstraint} reuses
   */
  protected final Map<SubcomponentSymbol, Optional<FormulaSolver.Satisfiability>> notImplied = new IdentityHashMap<>();

  public SubcomponentConstraintsImplied(@NotNull WithinComponentResolver withinComponentResolver,
                                        @NotNull PresenceResolver presenceResolver) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    for (SubcomponentSymbol subcomponent : component.getSubcomponents()) {
      if (!subcomponent.isTypePresent()) continue;
      Optional<FormulaSolver.Satisfiability> notImplied = this.checkNotImplied(solver, component, subcomponent);
      if (notImplied.isEmpty()) {
        Log.debug(() -> "Skip CoCo check, the type of a constraint is obscure.", LOG_NAME);
        continue;
      }
      if (notImplied.get() == FormulaSolver.Satisfiability.UNSATISFIABLE
        || this.hasNoValidConfiguration(solver, subcomponent.getType().getTypeInfo())) continue;
      ASTNode position = subcomponent.isPresentAstNode() ? subcomponent.getAstNode() : node;
      String message = VariableArcError.SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED.format(subcomponent.getName());
      switch (notImplied.get()) {
        case SATISFIABLE -> Log.error(message, position.get_SourcePositionStart(), position.get_SourcePositionEnd());
        case UNKNOWN -> Log.warn(message, position.get_SourcePositionStart(), position.get_SourcePositionEnd());
        case UNSATISFIABLE -> { }
      }
    }
  }

  /**
   * @param subcomponent a subcomponent with a type declared by the component
   *                     type
   * @return the satisfiability of a configuration of the component type in
   * which the subcomponent is present but its constraints are not satisfiable,
   * see {@link FormulaSolver#check}, or empty if the type of a constraint is
   * obscure; the result is calculated once per subcomponent
   * @param solver a solver that assumes the constraints of the component type
   */
  public Optional<FormulaSolver.Satisfiability> checkNotImplied(@NotNull FormulaSolver solver,
                                                                 @NotNull ComponentTypeSymbol component,
                                                                 @NotNull SubcomponentSymbol subcomponent) {
    Preconditions.checkNotNull(solver);
    Preconditions.checkNotNull(component);
    Preconditions.checkNotNull(subcomponent);
    Preconditions.checkArgument(subcomponent.isTypePresent());
    return this.notImplied.computeIfAbsent(subcomponent, s -> this.calculateNotImplied(solver, component, subcomponent));
  }

  protected Optional<FormulaSolver.Satisfiability> calculateNotImplied(@NotNull FormulaSolver solver,
                                                                       @NotNull ComponentTypeSymbol component,
                                                                       @NotNull SubcomponentSymbol subcomponent) {
    BoolExpr present = this.presenceResolver.presenceOf(component, subcomponent);
    BoolExpr constraints = this.presenceResolver.lift(component, subcomponent,
      this.presenceResolver.presenceOf(subcomponent.getType().getTypeInfo()));
    if (solver.isObscure(present) || solver.isObscure(constraints)) {
      return Optional.empty();
    }
    return Optional.of(solver.checkNotImplied(present, constraints, this.hiddenIn(subcomponent)));
  }

  protected boolean hasNoValidConfiguration(@NotNull FormulaSolver solver, @NotNull ComponentTypeSymbol type) {
    return solver.check(this.presenceResolver.presenceOf(type)) == FormulaSolver.Satisfiability.UNSATISFIABLE;
  }

  /**
   * @return whether a variable is internal to the subcomponent, i.e.,
   * neither one of its features nor one of its parameters
   */
  protected Predicate<String> hiddenIn(@NotNull SubcomponentSymbol subcomponent) {
    ComponentTypeSymbol type = subcomponent.getType().getTypeInfo();
    Set<String> visible = new HashSet<>();
    this.withinComponentResolver.getAll(type, ArcFeatureSymbol.class)
      .forEach(feature -> visible.add(PresenceResolver.qualify(subcomponent, feature.getName())));
    type.getParameterList().stream().map(VariableSymbol::getName)
      .forEach(parameter -> visible.add(PresenceResolver.qualify(subcomponent, parameter)));
    String prefix = PresenceResolver.qualify(subcomponent, "");
    return name -> name.startsWith(prefix) && !visible.contains(name);
  }
}
