/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util.trafo;

import arcbasis._ast.ASTComponentType;
import com.microsoft.z3.*;
import montiarc.conformance.AutomatonAbstractTest;
import montiarc.conformance.automaton2smt.smtAutomaton.SMTAutomaton;
import montiarc.conformance.util.AutomataLoader;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import montiarc._ast.ASTMACompilationUnit;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class GlobalVariableTrafoTest extends AutomatonAbstractTest {
  private final Context ctx = new Context();
  public String RELATIVE_MODEL_PATH = "test/resources/montiarc/conformance/";
  private ASTComponentType aut;
  private SMTAutomaton smtAut;
  private Expr<?> src;
  private Expr<?> tgt;
  private Expr<?> input;
  private Expr<?> output;

  @BeforeEach
  public void setup() {
    Log.init();
    initMills();
   // Given
    File autFile = new File(RELATIVE_MODEL_PATH + "util/trafo/Trafo.arc");
    File cdFile = new File(RELATIVE_MODEL_PATH + "util/trafo/Trafo.cd");

    Pair<ASTCDCompilationUnit, ASTMACompilationUnit> res =
        AutomataLoader.loadModels(autFile, cdFile);
    aut = res.getValue().getComponentType();
    smtAut = new SMTAutomaton(res.getValue().getComponentType(), res.getKey(), s -> s, ctx);

    src = ctx.mkConst("src_state", smtAut.getStateSort());
    tgt = ctx.mkConst("tgt_state", smtAut.getStateSort());
    input = ctx.mkConst("input", smtAut.getInputSort());
    output = ctx.mkConst("output", smtAut.getOutputSort());
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "1", "2"})
  public void TransitionNeedsTrafoTest() {
    // When
    ASTSCTransition transition = getTransition("0", aut);
    VariableSymbol counter = getGlobalVariable("counter", aut);

    BoolExpr smtTrans = smtAut.evaluateTransition(transition, input, src, tgt, output);
    BoolExpr contr =
        ctx.mkNot(
            ctx.mkEq(
                smtAut.getProperty(src, counter),
                smtAut.getProperty(tgt, counter))); // counter != counter

    Solver solver = ctx.mkSolver();
    solver.add(new BoolExpr[] {smtTrans, contr});

    //Then
    Assertions.assertEquals(solver.check(), Status.UNSATISFIABLE);
  }

  @Test
  public void TransitionDoNotNeedTrafoTest() {

    // When
    ASTSCTransition transition = getTransition("3", aut);
    BoolExpr smtTrans = smtAut.evaluateTransition(transition, input, src, tgt, output);
    Solver solver = ctx.mkSolver();
    solver.add(new BoolExpr[] {smtTrans});

    // Then
    Assertions.assertEquals(solver.check(), Status.SATISFIABLE);
  }
}
