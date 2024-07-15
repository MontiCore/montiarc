/* (c) https://github.com/MontiCore/monticore */
package mceffect.checker;

import com.microsoft.z3.*;
import montiarc.conformance.AutomataConfChecker;
import montiarc.conformance.automaton2smt.smtAutomaton.ChaosComplete;
import montiarc.conformance.automaton2smt.smtAutomaton.ICompleteSMTAut;
import montiarc.conformance.automaton2smt.smtAutomaton.ISMTAutomaton;
import montiarc.conformance.automaton2smt.smtAutomaton.SMTAutomaton;
import java.util.Map;
import mceffect.effect.Effect;
import mceffect.effect.EffectKind;
import montiarc._ast.ASTMACompilationUnit;

public class BasicEffectChecker implements EffectChecker {

  @Override
  public EffectCheckResult check(Effect effect) {
    ASTMACompilationUnit ma;
    // create automata
    Context ctx = AutomataConfChecker.buildContext();
    ISMTAutomaton aut = new SMTAutomaton(effect.getComponent().getAstNode(), null, s -> s, ctx);
    ICompleteSMTAut completeSMTAut =
        new ChaosComplete(effect.getComponent().getAstNode(), aut, ctx);

    // init states
    Expr<?> currState = ctx.mkConst("currState", aut.getStateSort());
    Expr<?> tgtSate1 = ctx.mkConst("nextState1", aut.getStateSort());
    Expr<?> tgtSate2 = ctx.mkConst("nextState2", aut.getStateSort());

    // init output
    Expr<?> output1 = ctx.mkConst("output1", aut.getOutputSort());
    Expr<?> output2 = ctx.mkConst("output2", aut.getOutputSort());
    Expr<?> subOutput1 = aut.getProperty(output1, effect.getTo());
    Expr<?> subOutput2 = aut.getProperty(output2, effect.getTo());

    // init input
    Expr<?> subInput1 = ctx.mkConst("input1", aut.getSort(effect.getFrom()));
    Expr<?> subInput2 = ctx.mkConst("input2", aut.getSort(effect.getFrom()));
    Expr<?> input1 = aut.mkConst(effect.getFrom(), Map.of(effect.getFrom(), subInput1));
    Expr<?> input2 = aut.mkConst(effect.getFrom(), Map.of(effect.getFrom(), subInput2));

    // build constraints
    BoolExpr trans1 = completeSMTAut.evaluate(input1, currState, tgtSate1, output1);
    BoolExpr trans2 = completeSMTAut.evaluate(input2, currState, tgtSate2, output2);
    BoolExpr diffOutput = ctx.mkNot(ctx.mkEq(subOutput1, subOutput2));
    BoolExpr diffTgtState = ctx.mkNot(ctx.mkEq(tgtSate2, tgtSate1));

    Solver solver = ctx.mkSolver();
    solver.add(trans1, trans2, ctx.mkOr(diffOutput, diffTgtState));

    Status status = solver.check();
    EffectCheckResult.Status res = EffectCheckResult.Status.UNKNOWN;
    String description =
        "The Actual implementation of the tool is not able to check if the source "
            + " port has an effect on the target port.\n";

    if (status == Status.UNSATISFIABLE) {
      if (effect.getEffectKind() == EffectKind.NO) {
        res = EffectCheckResult.Status.CORRECT;
        description = "The source port has no effect on the  target port";
      } else if (effect.getEffectKind() == EffectKind.MANDATORY) {
        res = EffectCheckResult.Status.INCORRECT;
        description = "The source port has no effect on the  target port";
      }
    }
    String trace = "";
    if (status == Status.SATISFIABLE) {
      trace =
          "Trace: \n First transition:\n"
              + printValues(solver.getModel(), currState, tgtSate1, input1, output1, aut)
              + "Second Transition\n"
              + printValues(solver.getModel(), currState, tgtSate2, input2, output2, aut);
    }
    return new EffectCheckResult(effect, res, description + trace);
  }

  @Override
  public boolean isApplicable(Effect effect) {
    return !effect.getComponent().isDecomposed() && effect.getEffectKind() != EffectKind.POTENTIAL;
  }

  private String printValues(
      Model model, Expr<?> curr, Expr<?> tgt, Expr<?> input, Expr<?> output, ISMTAutomaton aut) {
    return "\t\tFrom State: \t"
        + aut.print(model.eval(curr, true))
        + "\n"
        + "\t\tWith Input: \t"
        + aut.print(model.eval(input, true))
        + "\n"
        + "\t\tTo State:   \t"
        + aut.print(model.eval(tgt, true))
        + "\n"
        + "\t\tOutput:     \t"
        + aut.print(model.eval(output, true))
        + "\n";
  }
}
