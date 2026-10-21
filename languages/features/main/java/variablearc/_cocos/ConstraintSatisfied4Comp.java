/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.PresenceResolver;
import variablearc.smt.FormulaSolver;

/**
 * Checks that the constraints of a component type, including those of its
 * super components, are satisfiable.
 */
public class ConstraintSatisfied4Comp implements ArcBasisASTArcComponentTypeCoCo {

  protected static final String LOG_NAME = ConstraintSatisfied4Comp.class.getSimpleName();

  protected final PresenceResolver presenceResolver;

  public ConstraintSatisfied4Comp(@NotNull PresenceResolver presenceResolver) {
    this.presenceResolver = Preconditions.checkNotNull(presenceResolver);
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    FormulaSolver solver = this.presenceResolver.solver();
    BoolExpr constraints = this.presenceResolver.presenceOf(node.getSymbol());
    if (solver.isObscure(constraints)) {
      Log.debug(() -> "Skip CoCo check, the type of a constraint is obscure.", LOG_NAME);
      return;
    }
    switch (solver.check(constraints)) {
      case UNSATISFIABLE -> Log.error(VariableArcError.CONSTRAINT_NOT_SATISFIED.format(),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      case UNKNOWN -> Log.warn(VariableArcError.CONSTRAINT_NOT_SATISFIED.format(),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      case SATISFIABLE -> { }
    }
  }
}
