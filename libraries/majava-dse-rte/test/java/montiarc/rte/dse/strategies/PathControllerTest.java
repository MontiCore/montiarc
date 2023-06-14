/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse.strategies;

import com.microsoft.z3.*;
import dse.DSE;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.PathCondition;
import montiarc.rte.dse.TestController;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import montiarc.rte.timesync.InPort;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathControllerTest {

  MockPathCoverageController
    <List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>>,
      List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>>> controller;

  @BeforeEach
  void setUpMock(){
    controller = new MockPathCoverageController<>();
    assertThat(controller).isNotNull();

    controller.init();

    assertThat(controller).isNotNull();
  }

  @Test
  public void testStartTest() throws Exception {

    List<Expr<IntSort>> inputExpr = new ArrayList<>();
    List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>> expectedInput = new ArrayList<>();

    for (int i = 0; i < 2; i++) {
      inputExpr.add(TestController.getCtx()
        .mkConst("input_" + i, TestController.getCtx().getIntSort()));
      InPort<AnnotatedValue<Expr<IntSort>, Integer>> in = new InPort<>();
      in.update(AnnotatedValue.newAnnoValue(inputExpr.get(i), 0));
      expectedInput.add(in);
    }

    Context ctx = controller.getCtx();
    Solver solver = ctx.mkSolver();
    solver.check();

    Set<Pair<List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>>,
      List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>>>>
      result = controller.startTest(DSE.getInputValues(solver.getModel(), inputExpr),
      m -> DSE.getInputValues(m, inputExpr), DSE::runOnce).getInterestingInputs();

    assertThat(result).isNotNull();

    for (Pair<List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>>,
      List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>>> res : result) {
      for (int i = 0; i < 2; i++) {
        int finalI = i;

        assertAll(
          () -> assertThat(res.getKey().get(finalI).getValue()
            .getExpr()).isEqualTo(expectedInput.get(finalI).getValue().getExpr()),
          () -> assertThat(res.getKey().get(finalI).getValue()
            .getValue()).isEqualTo(expectedInput.get(finalI).getValue().getValue()),
          () -> assertThat(res.getValue().get(finalI).getValue()
            .getValue()).isEqualTo(expectedInput.get(finalI).getValue().getValue()),
          () -> assertThat(res.getValue().get(finalI).getValue()
            .getExpr()).isEqualTo(expectedInput.get(finalI).getValue().getExpr())
        );
      }
    }
  }

  @Test
  @Order(3)
  public void testGetIf() {

    BoolExpr expr = controller.getCtx().mkEq(controller.getCtx()
      .mkConst("input", controller.getCtx().mkIntSort()), controller.getCtx().mkInt(42));

    Boolean ifResult = controller.getIf(expr, 42 == 42, "testGetIf");
    BoolExpr condition = controller.getCtx().mkEq(controller.getCtx()
      .mkConst("input", controller.getCtx().mkIntSort()), controller.getCtx().mkInt(42));
    BoolExpr resultCondition = controller.getCtx()
      .mkEq(condition, controller.getCtx().mkBool(true));

    assertThat(ifResult).isEqualTo(true);
    assertThat(controller.branchingCondition.size()).isEqualTo(1);
    assertThat(controller.branchingCondition.get(0)
      .toString()).isEqualTo(resultCondition.toString());
  }

  @Test
  public void testGetIfOracle() {

    controller.getIfOracle("testgetIfOracle");

    BoolExpr condition = controller.getCtx()
      .mkEq(controller.getCtx().mkBoolConst("oracle_0"), controller.getCtx().mkBool(true));
    BoolExpr resultCondition = controller.getCtx()
      .mkEq(condition, controller.getCtx().mkBool(true));

    assertThat(controller.branchingCondition.size()).isEqualTo(1);
    assertThat(controller.branchingCondition.get(0)
      .toString()).isEqualTo(resultCondition.toString());
  }

  @Test
  public void testAddBranches() {

    BoolExpr branch = controller.getCtx()
      .mkEq(controller.getCtx().mkInt(42), controller.getCtx().mkInt(43));

    PathCondition expected = new PathCondition();
    expected.addBranch(branch, "testBranch");

    controller.addBranch(branch, "testBranch");

    assertThat(controller.takenBranches).isNotNull();
    assertThat(controller.takenBranches.toString()).isEqualTo(expected.toString());
  }
}