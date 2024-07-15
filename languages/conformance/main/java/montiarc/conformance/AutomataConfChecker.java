/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance;

import arcbasis._ast.ASTComponentType;
import com.microsoft.z3.*;
import montiarc.conformance.automaton2smt.smtAutomaton.ISMTAutomaton;
import montiarc.conformance.automaton2smt.smtAutomaton.SMTAutomaton;
import montiarc.conformance.util.AutomataUtils;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.se_rwth.commons.logging.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import montiarc.MontiArcMill;
import scmapping.mapping2smt.AutomataMapping;
import scmapping.mapping2smt.IDMapping;

public class AutomataConfChecker {

  public static Context buildContext() {
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");
    return new Context(cfg);
  }

  private String printValues(
      Model model,
      Expr<? extends Sort> currState,
      Expr<? extends Sort> nextState,
      Expr<? extends Sort> input,
      Expr<?> output,
      ISMTAutomaton aut,
      AutomataMapping mapping) {
    String result =
        "\t\tFrom State: \t"
            + aut.print(model.eval(mapping.mapState(currState), true))
            + "\n"
            + "\t\tWith Input: \t"
            + aut.print(model.eval(mapping.mapInput(input), true))
            + "\n"
            + "\t\tTo State:   \t"
            + aut.print(model.eval(mapping.mapState(nextState), true))
            + "\n"
            + "\t\tOutput:     \t"
            + aut.print(model.eval(mapping.mapOutput(output), true))
            + "\n";

    return result;
  }

  public boolean isConform(
      ASTComponentType referenceAut,
      ASTComponentType concreteAut,
      ASTCDCompilationUnit refCD,
      ASTCDCompilationUnit conCD,
      AutomataMapping mapping) {
    Context ctx = buildContext();

    // convert reference automaton to smt
    ISMTAutomaton ref = new SMTAutomaton(referenceAut, refCD, s -> s + "_ref", ctx);

    // convert concrete automaton in smt
    ISMTAutomaton con = new SMTAutomaton(concreteAut, conCD, s -> s + "_con", ctx);

    // declare-Const
    Expr<? extends Sort> currState = ctx.mkConst("curr_state", con.getStateSort());
    Expr<? extends Sort> nextState = ctx.mkConst("next_state", con.getStateSort());
    Expr<? extends Sort> input = ctx.mkConst("input", con.getInputSort());
    Expr<?> output = ctx.mkConst("output", con.getOutputSort());

    // convert the mapping in smt
    BoolExpr[] map = mapping.init(con, ref, input, output, currState, nextState, ctx);

    // build solver constraints
    List<BoolExpr> solverConstraints = new java.util.ArrayList<>(List.of(map));

    // negate reference transition and add to the solver
    for (ASTSCTransition trans : AutomataUtils.getTransitions(referenceAut)) {
      BoolExpr constr =
          ctx.mkNot(
              ref.evaluateTransition(
                  trans,
                  mapping.mapInput(input),
                  mapping.mapState(currState),
                  mapping.mapState(nextState),
                  mapping.mapOutput(output)));

      solverConstraints.add(constr);
    }
    boolean isConform = true;
    for (ASTSCTransition transition : AutomataUtils.getTransitions(concreteAut)) {
      BoolExpr constraint = con.evaluateTransition(transition, input, currState, nextState, output);
      solverConstraints.add(constraint);
      Solver solver = ctx.mkSolver();
      solver.add(solverConstraints.toArray(new BoolExpr[0]));

      if (solver.check() == Status.SATISFIABLE) {
        isConform = false;
        Log.warn(
            "Transition is *NOT* conform!\n"
                + transition.get_SourcePositionStart().toString()
                + ": "
                + MontiArcMill.prettyPrint(transition, false)
                + "\n"
                + "\n"
                + "Possible Transition in concrete Model ("
                + AutomataUtils.getName(concreteAut)
                + "):\n"
                + printValues(
                    solver.getModel(),
                    currState,
                    nextState,
                    input,
                    output,
                    con,
                    IDMapping.getIDMapping())
                + "\n"
                + "Impossible Transition in Reference Model ("
                + AutomataUtils.getName(referenceAut)
                + "):\n"
                + printValues(
                    solver.getModel(), currState, nextState, input, output, ref, mapping));
      } else {
        Log.trace(
            "The transition " + transition.get_SourcePositionStart().toString() + " is conform",
            this.getClass().getName());
      }
      solverConstraints.remove(constraint);
    }
    return isConform;
  }
}
