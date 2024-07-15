/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.microsoft.z3.*;
import montiarc.conformance.AutomatonAbstractTest;
import montiarc.conformance.automaton2smt.smtAutomaton.SMTAutomaton;
import de.monticore.scbasis._ast.ASTSCTransition;
import java.io.File;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SMTAutomatonTest extends AutomatonAbstractTest {
  protected Context ctx;
  protected String modelDir = "test/resources/montiarc/conformance/automaton2smt/";
  protected Expr<? extends Sort> src;
  protected Expr<? extends Sort> tgt;
  protected Expr<? extends Sort> input;
  protected Expr<?> output;

  public static Stream<Arguments> conTransitions() {
    return Stream.of(
             Arguments.of(
            "0",
            "password=\"correct\"",
            "Anon{counter=1000}",
            "Known{counter=1000}",
            "output = [ERROR], value = [10]"),
        Arguments.of(
            "1",
            "input=GET_VALUE",
            "Known{counter=1000}",
            "Known{counter=1000}",
            "output = [], value = [1000]"),
        Arguments.of(
            "2",
            "input=INCREASE_VALUE",
            "Known{counter=1000}",
            "Known{counter=1001}",
            "output = [], value = [1000]"),
        Arguments.of(
            "3",
            "input=LOGOUT",
            "Known{counter=1000}",
            "Anon{counter=1000}",
            "output = [], value = []"),
        Arguments.of(
            "4",
            "input=LOGOUT",
            "Anon{counter=1000}",
            "Anon{counter=1000}",
            "output = [], value = []"),
        Arguments.of(
            "5",
            "input=INCREASE_VALUE",
            "Anon{counter=1000}",
            "Anon{counter=1000}",
            "output = [ERROR], value = []"));
  }

  public static Stream<Arguments> refTransitions() {
    return Stream.of(
        Arguments.of("0", "input=LOGIN", "NotLoggedIn", "LoggedIn", "output = []"),
        Arguments.of("1", "input=ACTION", "LoggedIn", "LoggedIn", "output = [RESPONSE]"),
        Arguments.of("2", "input=LOGOUT", "LoggedIn", "NotLoggedIn", "output = []"),
        Arguments.of("3", "input=ACTION", "NotLoggedIn", "NotLoggedIn", "output = [ERROR]"),
        Arguments.of("4", "input=LOGOUT", "NotLoggedIn", "NotLoggedIn", "output = []"));
  }

  @BeforeEach
  public void setup() {
    initMills();

    // Given
    File conAutFile = new File(modelDir + "concrete/Concrete.arc");
    File conCDFile = new File(modelDir + "concrete/Datatypes.cd");
    File refAutFile = new File(modelDir + "reference/Reference.arc");
    File refCDFile = new File(modelDir + "reference/Datatypes.cd");
    loadModels(refAutFile, refCDFile, conAutFile, conCDFile);
    ctx = buildContext();

    // When
    con = new SMTAutomaton(conAut, conCD, s -> s + "_con", ctx);
    ref = new SMTAutomaton(refAut, refCD, s -> s + "_ref", ctx);
  }

  @ParameterizedTest
  @MethodSource("refTransitions")
  public void testRefTransitions2smt(String nr, String in, String curr, String next, String out) {
    // When
    src = ctx.mkConst("src_state", ref.getStateSort());
    tgt = ctx.mkConst("tgt_state", ref.getStateSort());
    input = ctx.mkConst("input", ref.getInputSort());
    output = ctx.mkConst("output", ref.getOutputSort());

    Solver solver = ctx.mkSolver();
    solver.add(ref.evaluateTransition(getTransition(nr, refAut), input, src, tgt, output));
    assertEquals(Status.SATISFIABLE, solver.check());
    Model model = solver.getModel();

    // Then
    assertEquals(curr, ref.print(model.eval(src, true)));
    assertEquals(next, ref.print(model.eval(tgt, true)));
    assertEquals(in, ref.print(model.eval(input, true)));
    assertEquals(out, ref.print(model.eval(output, true)));
  }

  @ParameterizedTest
  @MethodSource("conTransitions")
  public void testConTransitions2smt(String nr, String in, String curr, String next, String out) {
    // When
    src = ctx.mkConst("src_state", con.getStateSort());
    tgt = ctx.mkConst("tgt_state", con.getStateSort());
    input = ctx.mkConst("input", con.getInputSort());
    output = ctx.mkConst("output", con.getOutputSort());
    Solver solver = ctx.mkSolver();

    // make sure the counter is initially 0
    BoolExpr constr = ctx.mkEq(con.getStateSort().getAccessors()[0][1].apply(src), ctx.mkInt(1000));
    solver.add(constr);
    BoolExpr trans = con.evaluateTransition(getTransition(nr, conAut), input, src, tgt, output);
    solver.add(trans);

    // Then
    assertEquals(Status.SATISFIABLE, solver.check());
    Model model = solver.getModel();
    assertEquals(curr, con.print(model.eval(src, true)));
    assertEquals(next, con.print(model.eval(tgt, true)));
    assertEquals(in, con.print(model.eval(input, true)));
    assertEquals(out, con.print(model.eval(output, true)));
  }

  @Test
  void internalStateInTransitionBeforeAndAfter() {
    // When
    src = ctx.mkConst("src_state", con.getStateSort());
    tgt = ctx.mkConst("tgt_state", con.getStateSort());
    input = ctx.mkConst("input", con.getInputSort());
    output = ctx.mkConst("output", con.getOutputSort());

    Solver solver = ctx.mkSolver();
    ASTSCTransition transition = getTransition("2", conAut);
    BoolExpr expr = con.evaluateTransition(transition, input, src, src, output);
    solver.add(expr);
    Status status = solver.check();

    // Then
    assertEquals(Status.UNSATISFIABLE, status);
  }

  @Test
  void internalStateInTransitionBeforeAndAfter_sat() {
    // When
    src = ctx.mkConst("src_state", con.getStateSort());
    tgt = ctx.mkConst("tgt_state", con.getStateSort());
    input = ctx.mkConst("input", con.getInputSort());
    output = ctx.mkConst("output", con.getOutputSort());

    Solver solver = ctx.mkSolver();
    ASTSCTransition transition = getTransition("2", conAut);
    BoolExpr expr = con.evaluateTransition(transition, input, src, tgt, output);
    solver.add(expr);

    // Then
    Status status = solver.check();
    System.out.println(status);
    assertEquals(Status.SATISFIABLE, status);
  }
}
