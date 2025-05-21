/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt.smtAutomaton;

import arcbasis._ast.ASTArcComponentType;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import de.monticore.scbasis._ast.ASTSCTransition;
import montiarc.conformance.util.AutomataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This completion doesn't handle Partiality at all. Bad things can (and will) happen for partial
 * automata!
 */
public class InvalidOnPartialComplete implements ICompleteSMTAut {
  private final ASTArcComponentType comp;
  private final ISMTAutomaton smtAut;
  private final Context ctx;

  public InvalidOnPartialComplete(ASTArcComponentType comp, ISMTAutomaton smtAut, Context ctx) {
    this.comp = comp;
    this.smtAut = smtAut;
    this.ctx = ctx;
  }

  @Override
  public BoolExpr evaluate(Expr<?> in, Expr<?> state, Expr<?> nextState, Expr<?> out) {
    List<BoolExpr> transitions = new ArrayList<>();
    for (ASTSCTransition trans : AutomataUtils.getTransitions(comp)) {
      transitions.add(
          ctx.mkAnd(
              smtAut.evaluateGuard(trans, in, state),
              smtAut.evaluateAction(trans, in, state, nextState, out)));
    }
    return ctx.mkOr(transitions.toArray(new BoolExpr[0]));
  }
}
