/* (c) https://github.com/MontiCore/monticore */
package effect;

import static montiarc.conformance.util.AutomataLoader.loadModels;
import static org.junit.jupiter.api.Assertions.fail;

import com.microsoft.z3.*;
import montiarc.conformance.AutomataConfChecker;
import montiarc.conformance.automaton2smt.smtAutomaton.ChaosComplete;
import montiarc.conformance.automaton2smt.smtAutomaton.ICompleteSMTAut;
import montiarc.conformance.automaton2smt.smtAutomaton.ISMTAutomaton;
import montiarc.conformance.automaton2smt.smtAutomaton.SMTAutomaton;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EffectTest {
  @BeforeEach
  public void setup() {
    Log.init();
    initMills();
  }

  public void initMills() {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();

    MontiArcMill.reset();
    MontiArcMill.init();
    MontiArcMill.globalScope().clear();
  }

  @Test
  public void checkEffect() throws IOException {

    // Given
    File maFile = Path.of("test/resources/effect", "Effect.arc").toFile();
    File cdFile = Path.of("test/resources/effect", "Effect.cd").toFile();

    Pair<ASTCDCompilationUnit, ASTMACompilationUnit> asts = loadModels(maFile, cdFile);
    PortSymbol inputPort = MontiArcMill.globalScope().resolvePort("effect.Effect.input").get();
    PortSymbol outputPort = MontiArcMill.globalScope().resolvePort("effect.Effect.output").get();

    Context ctx = AutomataConfChecker.buildContext();

    // When
    ISMTAutomaton smtAut =
        new SMTAutomaton(asts.getRight().getComponentType(), asts.getLeft(), s -> s, ctx);

    Expr<?> initState = ctx.mkConst("initState", smtAut.getStateSort());

    Expr<?> nextState1 = ctx.mkConst("nextState1", smtAut.getStateSort());
    Expr<?> input1 = ctx.mkConst("input1", smtAut.getInputSort());
    Expr<?> outputItem1 = ctx.mkConst("outputItem1", ctx.mkIntSort());
    Expr<?> output1 = ctx.mkConst("output1", smtAut.getOutputSort());
    BoolExpr stupidEq1 = ctx.mkEq(smtAut.getProperty(output1, outputPort), ctx.mkUnit(outputItem1));

    Expr<?> nextState2 = ctx.mkConst("nextState2", smtAut.getStateSort());
    Expr<?> input2 = ctx.mkConst("input2", smtAut.getInputSort());
    Expr<?> outputItem2 = ctx.mkConst("outputItem2", ctx.mkIntSort());
    Expr<?> output2 = ctx.mkConst("output2", smtAut.getOutputSort());
    BoolExpr stupidEq2 = ctx.mkEq(smtAut.getProperty(output2, outputPort), ctx.mkUnit(outputItem2));

    // Then
    ICompleteSMTAut completeSMTAut =
        new ChaosComplete(asts.getRight().getComponentType(), smtAut, ctx);

    BoolExpr trans1 = completeSMTAut.evaluate(input1, initState, nextState1, output1);
    BoolExpr trans2 = completeSMTAut.evaluate(input2, initState, nextState2, output2);

    BoolExpr input_small_difference =
        ctx.mkLe(
            ctx.mkAdd(
                (IntExpr) smtAut.getProperty(input1, inputPort),
                ctx.mkUnaryMinus((IntExpr) smtAut.getProperty(input2, inputPort))),
            ctx.mkInt(2));

    BoolExpr output_large_difference =
        ctx.mkGt(
            ctx.mkAdd((IntExpr) outputItem1, ctx.mkUnaryMinus((IntExpr) outputItem2)),
            ctx.mkInt(2));

      Solver solver = ctx.mkSolver();
    solver.add(
        trans1, trans2, input_small_difference, output_large_difference, stupidEq1, stupidEq2);
    Status status = solver.check();
    if (status == Status.SATISFIABLE) {
      Model model = solver.getModel();
      System.out.println(
          "Found a counterexample! \n "
              + "From state "
              + smtAut.print(initState)
              + "\n"
              + "\n"
              + "There are two different inputs with small variations, which lead to large difference: \n"
              + "1) Input: "
              + smtAut.print(model.eval(input1, true))
              + "\t\n Output: "
              + smtAut.print(model.eval(output1, true))
              + "\n"
              + "2) Input: "
              + smtAut.print(model.eval(input2, true))
              + "\t\n Output: "
              + smtAut.print(model.eval(output2, true))
              + "\n");
    } else {
      System.out.println("Found not counterexample");
      fail();
    }
  }
}
