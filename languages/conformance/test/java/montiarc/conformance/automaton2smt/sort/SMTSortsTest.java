/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.automaton2smt.sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.microsoft.z3.*;
import montiarc.conformance.AutomatonAbstractTest;
import montiarc.conformance.automaton2smt.cd.CD2SMT;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SMTSortsTest extends AutomatonAbstractTest {
  protected Context ctx;
  protected String RELATIVE_MODEL_PATH =
      "test/resources/montiarc/conformance/automaton2smt/";
  protected CD2SMT conCd2SMT;
  protected Function<String, String> conIdent = s -> s + "_con";

  protected CD2SMT refCd2SMT;
  protected Function<String, String> refIdent = s -> s + "_con";

  @BeforeEach
  public void setup() {
    initMills();

    // Given
    File conAutFile = new File(RELATIVE_MODEL_PATH + "concrete/Concrete.arc");
    File conCDFile = new File(RELATIVE_MODEL_PATH + "concrete/Datatypes.cd");
    File refAutFile = new File(RELATIVE_MODEL_PATH + "reference/Reference.arc");
    File refCDFile = new File(RELATIVE_MODEL_PATH + "reference/Datatypes.cd");
    loadModels(refAutFile, refCDFile, conAutFile, conCDFile);
    ctx = buildContext();

    conIdent = s -> s + "_con";
    conCd2SMT = new CD2SMT(conCD, conIdent, ctx);

    refIdent = s -> s + "_ref";
    refCd2SMT = new CD2SMT(refCD, refIdent, ctx);
  }

  @Test
  public void referenceStates2smtTest() {
    // When
    StateSort stateSort = new StateSort(refAut, refCd2SMT, ctx, refIdent);

    // Then
    String constructor = stateSort.constructor.ConstructorDecl().toString();
    assertEquals("(declare-fun State (SubState_ref) StateSort_ref)", constructor);
  }

  @Test
  public void concreteStates2smtTest() {
    // When
    StateSort stateSort = new StateSort(conAut, conCd2SMT, ctx, conIdent);
    String constructor = stateSort.constructor.ConstructorDecl().toString();

    //Then
    assertEquals("(declare-fun State (SubState_con Int) StateSort_con)", constructor);
  }

  @Test
  public void referenceInput2smtTest() {
    // When
    InputSort inputSort = new InputSort(refAut, refCd2SMT, ctx, refIdent);
    String constructor = inputSort.getSort().getConstructors()[0].toString();

    // Then
    assertEquals("(declare-fun input_ref (Input_ref) InputSort_ref)", constructor);
  }

  @Test
  public void concreteInput2smtTest() {
    // When
    InputSort inputSort = new InputSort(conAut, conCd2SMT, ctx, conIdent);
    List<String> constrList = new ArrayList<>();
    Arrays.stream(inputSort.getSort().getConstructors()).forEach(c -> constrList.add(c.toString()));

    // Then
    assertTrue(constrList.contains("(declare-fun input_con (Input_con) InputSort_con)"));
    assertTrue(constrList.contains("(declare-fun password_con (String) InputSort_con)"));
  }

  @Test
  public void referenceOutput2smtTest() {
    // When
    OutputSort outputSort = new OutputSort(refAut, refCd2SMT, ctx, refIdent);
    String constructor = outputSort.getSort().getConstructors()[0].toString();

    // Then
    assertEquals("(declare-fun Output_ref ((Seq Output_ref)) OutputSort_ref)", constructor);
  }

  @Test
  public void concreteOutput2smtTest() {
    // When
    OutputSort outputSort = new OutputSort(conAut, conCd2SMT, ctx, conIdent);
    String constr1 = outputSort.getSort().getConstructors()[0].toString();

    //Then
    assertEquals("(declare-fun Output_con ((Seq Output_con) (Seq Int)) OutputSort_con)", constr1);
  }
}
