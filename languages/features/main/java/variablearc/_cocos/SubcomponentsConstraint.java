/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Checks that the constraints of a component type determine the features of
 * its present subcomponents, i.e., that no configuration of the component
 * type admits two different assignments of a subcomponent's feature.
 */
public class SubcomponentsConstraint implements ArcBasisASTArcComponentTypeCoCo {

  protected static final String LOG_NAME = SubcomponentsConstraint.class.getSimpleName();

  protected final WithinComponentResolver withinComponentResolver;
  protected final PresenceResolver presenceResolver;
  protected final SubcomponentConstraintsImplied subcomponentConstraintsImplied;

  public SubcomponentsConstraint(@NotNull WithinComponentResolver withinComponentResolver,
                                 @NotNull PresenceResolver presenceResolver) {
    this(withinComponentResolver, presenceResolver,
      new SubcomponentConstraintsImplied(withinComponentResolver, presenceResolver));
  }

  /**
   * @param subcomponentConstraintsImplied the CoCo that reports the
   *                                       subcomponents excluded by this one,
   *                                       whose results are reused
   */
  public SubcomponentsConstraint(@NotNull WithinComponentResolver withinComponentResolver,
                                 @NotNull PresenceResolver presenceResolver,
                                 @NotNull SubcomponentConstraintsImplied subcomponentConstraintsImplied) {
    this.withinComponentResolver = Preconditions.checkNotNull(withinComponentResolver);
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
    this.subcomponentConstraintsImplied = Preconditions.checkNotNull(subcomponentConstraintsImplied);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    List<String> undetermined = new ArrayList<>();
    List<String> possiblyUndetermined = new ArrayList<>();
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    if (solver.isObscure(this.presenceResolver.presenceOf(component))) {
      Log.debug(() -> "Skip CoCo check, the type of a constraint is obscure.", LOG_NAME);
      return;
    }
    Map<String, List<SubcomponentSymbol>> features = this.featuresOfSubcomponents(solver, component);
    for (Map.Entry<String, List<SubcomponentSymbol>> feature : features.entrySet()) {
      BoolExpr present = solver.or(feature.getValue().stream()
        .map(subcomponent -> this.presenceResolver.presenceOf(component, subcomponent))
        .toList());
      switch (solver.checkUndetermined(present, features.keySet(), feature.getKey())) {
        case SATISFIABLE -> undetermined.add(feature.getKey());
        case UNKNOWN -> possiblyUndetermined.add(feature.getKey());
        case UNSATISFIABLE -> { }
      }
    }
    if (!undetermined.isEmpty()) {
      Log.error(VariableArcError.SUBCOMPONENTS_NOT_CONSTRAINT.format(undetermined.toString()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
    if (!possiblyUndetermined.isEmpty()) {
      Log.warn(VariableArcError.SUBCOMPONENTS_NOT_CONSTRAINT.format(possiblyUndetermined.toString()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }

  /**
   * @return the subcomponents declared by the component type by the names of
   * their features in its vocabulary, which same-named subcomponents share,
   * excluding subcomponents whose constraints the component type does not
   * imply, which {@link SubcomponentConstraintsImplied} reports
   */
  protected Map<String, List<SubcomponentSymbol>> featuresOfSubcomponents(@NotNull FormulaSolver solver,
                                                                          @NotNull ComponentTypeSymbol component) {
    Map<String, List<SubcomponentSymbol>> features = new LinkedHashMap<>();
    for (SubcomponentSymbol subcomponent : component.getSubcomponents()) {
      if (!subcomponent.isTypePresent()) continue;
      if (!this.subcomponentConstraintsImplied.checkNotImplied(solver, component, subcomponent)
        .equals(Optional.of(FormulaSolver.Satisfiability.UNSATISFIABLE))) continue;
      for (ArcFeatureSymbol feature : this.withinComponentResolver.getAll(subcomponent.getType().getTypeInfo(),
        ArcFeatureSymbol.class)) {
        features.computeIfAbsent(PresenceResolver.qualify(subcomponent, feature.getName()), name -> new ArrayList<>())
          .add(subcomponent);
      }
    }
    return features;
  }
}
