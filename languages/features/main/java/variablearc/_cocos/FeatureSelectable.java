/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

/**
 * Warns about features of a component type that are not selected in any of
 * its valid configurations. Components without valid configurations are left
 * to {@link ConstraintSatisfied4Comp}.
 */
public class FeatureSelectable implements ArcBasisASTArcComponentTypeCoCo {

  protected static final String LOG_NAME = FeatureSelectable.class.getSimpleName();

  protected final PresenceResolver presenceResolver;

  public FeatureSelectable(@NotNull PresenceResolver presenceResolver) {
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    if (!(component.getSpannedScope() instanceof IVariableArcScope scope)) return;
    if (scope.getLocalArcFeatureSymbols().isEmpty()) return;
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr constraints = this.presenceResolver.presenceOf(component);
    if (solver.isObscure(constraints)) {
      Log.debug(() -> "Skip CoCo check, the type of a constraint is obscure.", LOG_NAME);
      return;
    }
    if (solver.check(constraints) == FormulaSolver.Satisfiability.UNSATISFIABLE) return;
    for (ArcFeatureSymbol feature : scope.getLocalArcFeatureSymbols()) {
      BoolExpr present = this.presenceResolver.presenceOf(component, feature);
      if (solver.checkWith(present, feature.getName()) == FormulaSolver.Satisfiability.UNSATISFIABLE) {
        ASTNode position = feature.isPresentAstNode() ? feature.getAstNode() : node;
        Log.warn(VariableArcError.FEATURE_NOT_SELECTABLE.format(feature.getName()),
          position.get_SourcePositionStart(), position.get_SourcePositionEnd());
      }
    }
  }
}
