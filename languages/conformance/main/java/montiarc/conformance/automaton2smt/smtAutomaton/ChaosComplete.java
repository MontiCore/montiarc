/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt.smtAutomaton;

import arcbasis._ast.ASTComponentType;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import montiarc.conformance.util.AutomataUtils;
import de.monticore.scbasis._ast.ASTSCTransition;

import java.util.ArrayList;
import java.util.List;

public class ChaosComplete implements ICompleteSMTAut {
  private final ASTComponentType comp;
  private final ISMTAutomaton smtAut;
  private final Context ctx;

  public ChaosComplete(ASTComponentType comp, ISMTAutomaton smtAut, Context ctx) {
    this.comp = comp;
    this.smtAut = smtAut;
    this.ctx = ctx;
  }

  @Override
  public BoolExpr evaluate(Expr<?> in, Expr<?> state, Expr<?> nextState, Expr<?> out) {
    List<BoolExpr> transitions = new ArrayList<>();
    List<BoolExpr> guards = new ArrayList<>();
    for (ASTSCTransition trans : AutomataUtils.getTransitions(comp)) {
      guards.add(smtAut.evaluateGuard(trans, in, state));
      transitions.add(smtAut.evaluateTransition(trans, in, state, nextState, out));
    }
    {
      // Automata is partial iff no guard is valid
      BoolExpr partialCase = ctx.mkNot(ctx.mkOr(guards.toArray(new BoolExpr[0])));
      // Add "fake" partial transition to the transitions Expr. For the Action anything is allowed.
      transitions.add(partialCase);
    }
    return ctx.mkOr(transitions.toArray(new BoolExpr[0]));
  }
}
