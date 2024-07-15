/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.scmapping;

import static montiarc.conformance.util.AutomataLoader.loadMapping;

import com.microsoft.z3.*;
import montiarc.conformance.AutomatonAbstractTest;
import montiarc.conformance.automaton2smt.smtAutomaton.SMTAutomaton;
import de.monticore.scbasis._symboltable.SCStateSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ISymbol;
import java.io.File;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import scmapping.mapping2smt.AutomataMapping;
import scmapping.mapping2smt.MCMapping;

class MappingTest extends AutomatonAbstractTest {
  public String modelDir = "test/resources/montiarc/conformance/";
  private Context ctx;
  private Expr<? extends Sort> currState;
  private Expr<? extends Sort> nextState;
  private Expr<? extends Sort> input;
  private Expr<?> output;
  private AutomataMapping mapping;

  private BoolExpr[] mappingConstraints;

  @BeforeEach
  public void setup() {
    initMills();

    // Given
    File conAutFile = new File(modelDir + "automaton2smt/concrete/Concrete.arc");
    File conCDFile = new File(modelDir + "automaton2smt/concrete/Datatypes.cd");
    File refAutFile = new File(modelDir + "automaton2smt/reference/Reference.arc");
    File refCDFile = new File(modelDir + "automaton2smt/reference/Datatypes.cd");
    String mappingFile = modelDir + "scmapping/" + "mapping.map";

    // When
    loadModels(refAutFile, refCDFile, conAutFile, conCDFile);
    ctx = buildContext();
    con = new SMTAutomaton(conAut, conCD, s -> s + "_con", ctx);
    ref = new SMTAutomaton(refAut, refCD, s -> s + "_ref", ctx);

    currState = ctx.mkConst("curr_state", con.getStateSort());
    nextState = ctx.mkConst("next_state", con.getStateSort());
    input = ctx.mkConst("input", con.getInputSort());
    output = ctx.mkConst("output", con.getOutputSort());

    //load mapping
    mapping = new MCMapping(loadMapping(mappingFile, refAut, refCD, conAut, conCD));
    mappingConstraints = mapping.init(con, ref, input, output, currState, nextState, ctx);
  }



  @ParameterizedTest
  @ValueSource(strings = {"mcMapping"})
  public void stateMappingTest(String strategy) {
    //Then
    Assertions.assertEquals(getRefStateName("Anon"), "NotLoggedIn");
    Assertions.assertEquals(getRefStateName("Known"), "LoggedIn");
    Assertions.assertNotEquals(getRefStateName("Anon"), "LoggedIn");
    Assertions.assertNotEquals(getRefStateName("Known"), "NotLoggedIn");
  }

  @ParameterizedTest
  @ValueSource(strings = {"mcMapping"})
  public void inputMappingTest(String strategy) {
    //Then
    Assertions.assertEquals(getRefInput("GET_VALUE"), "input=ACTION");
    Assertions.assertEquals(getRefInput("LOGOUT"), "input=LOGOUT");
    Assertions.assertEquals(getRefInput("INCREASE_VALUE"), "input=ACTION");
    Assertions.assertEquals(getRefInput("correct"), "input=LOGIN");
    Assertions.assertNotEquals(getRefInput("GET_VALUE"), "input=LOGIN");
  }

  @ParameterizedTest
  @ValueSource(strings = {"mcMapping"})
  public void outputMappingTest(String strategy) {
    //Then
    Assertions.assertEquals(getRefOutput("ERROR"), "output = [ERROR]");
    Assertions.assertEquals(getRefOutput("0"), "output = [RESPONSE, RESPONSE]");
    Assertions.assertEquals(getRefOutput("[]"), "output = []");
  }

  public String getRefStateName(String conStateName) {
    SCStateSymbol stateSymbol = getState(conStateName, conAut);
    BoolExpr constraint = con.checkConstructor(currState, stateSymbol);
    Model model = solve(ctx.mkSolver(), constraint);
    return ref.print(model.eval(mapping.mapState(currState), true));
  }

  public String getRefInput(String conInputName) {
    PortSymbol port;
    Map<ISymbol, Expr<?>> args = new HashMap<>();

    if (conInputName.equals("correct") || conInputName.equals("not_correct")) {
      port = getInputPort("password", conAut);
      args.put(port, ctx.mkString(conInputName));
    } else {
      port = getInputPort("input", conAut);
      FieldSymbol enumConst = getEnumConst(conInputName, "Input", conCD);
      args.put(port, con.mkConst(enumConst));
    }

    Expr<?> conInputExpr = con.mkConst(port, args);
    Model model = solve(ctx.mkSolver(), ctx.mkEq(input, conInputExpr));
    return ref.print(model.eval(mapping.mapInput(input), true));
  }

  public String getRefOutput(String conOutputName) {
    PortSymbol port;
    Map<ISymbol, Expr<?>> args = new HashMap<>();

    if (conOutputName.equals("0")) {
      port = getOutputPort("value", conAut);
      args.put(port, ctx.mkInt(0));

    } else if (conOutputName.equals("[]")) {
      port = getOutputPort("value", conAut);
    } else {
      port = getOutputPort("output", conAut);
      FieldSymbol enumConst = getEnumConst(conOutputName, "Output", conCD);
      args.put(port, con.mkConst(enumConst));
    }

    Expr<?> conInputExpr = con.mkConst(port, args);
    Model model = solve(ctx.mkSolver(), ctx.mkEq(output, conInputExpr));
    return ref.print(model.eval(mapping.mapOutput(output), true));
  }

  public Model solve(Solver solver, BoolExpr constraint) {
    List<BoolExpr> solverConstraints = new ArrayList<>(List.of(mappingConstraints));

    solverConstraints.add(constraint);

    solver.add(solverConstraints.toArray(new BoolExpr[0]));
    Assertions.assertEquals(solver.check(), Status.SATISFIABLE);
    return solver.getModel();
  }
}
