/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentBody;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcVarIf;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

/**
 * Warns about branches of varif statements that are not present in any valid
 * configuration of their component type. Branches nested in such a branch
 * are not reported again. Components without valid configurations are left
 * to {@link ConstraintSatisfied4Comp}.
 */
public class VariationPointPresent implements ArcBasisASTArcComponentTypeCoCo {

  protected static final String LOG_NAME = VariationPointPresent.class.getSimpleName();

  protected final PresenceResolver presenceResolver;

  public VariationPointPresent(@NotNull PresenceResolver presenceResolver) {
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ComponentTypeSymbol component = node.getSymbol();
    FormulaSolver solver = this.presenceResolver.solverFor(component);
    BoolExpr constraints = this.presenceResolver.presenceOf(component);
    if (solver.isObscure(constraints)) {
      Log.debug(() -> "Skip CoCo check, the type of a constraint is obscure.", LOG_NAME);
      return;
    }
    if (solver.check(constraints) == FormulaSolver.Satisfiability.UNSATISFIABLE) return;
    this.check(solver, component, node.getBody());
  }

  protected void check(@NotNull FormulaSolver solver,
                       @NotNull ComponentTypeSymbol component,
                       @NotNull ASTArcElement element) {
    if (element instanceof ASTComponentBody body) {
      body.getArcElementList().forEach(child -> this.check(solver, component, child));
    } else if (element instanceof ASTArcVarIf varIf) {
      this.checkBranch(solver, component, varIf.getThen());
      if (varIf.isPresentOtherwise()) {
        this.checkBranch(solver, component, varIf.getOtherwise());
      }
    }
  }

  protected void checkBranch(@NotNull FormulaSolver solver,
                             @NotNull ComponentTypeSymbol component,
                             @NotNull ASTArcElement branch) {
    BoolExpr presence = this.presenceResolver.presenceOf(component, branch);
    if (solver.isObscure(presence)) {
      Log.debug(() -> "Skip CoCo check, the type of a varif condition is obscure.", LOG_NAME);
    } else if (solver.check(presence) == FormulaSolver.Satisfiability.UNSATISFIABLE) {
      Log.warn(VariableArcError.VARIATION_POINT_NOT_PRESENT.format(),
        branch.get_SourcePositionStart(), branch.get_SourcePositionEnd());
    } else {
      this.check(solver, component, branch);
    }
  }
}
