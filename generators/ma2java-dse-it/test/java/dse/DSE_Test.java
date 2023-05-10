/* (c) https://github.com/MontiCore/monticore */
package dse;

import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import com.microsoft.z3.Solver;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.TestController;
import montiarc.rte.dse.strategie.MockPathCoverageController;
import montiarc.rte.dse.strategie.PathCoverageController;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import montiarc.rte.timesync.InPort;
import montiarc.rte.timesync.OutPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DSE_Test {

  static Context ctx;

  @BeforeEach
  void setUpMock() throws Exception {
    PathCoverageController<List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>>,
      List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>>>
      controller = MockPathCoverageController.init(DSE::runOnce);

    ctx = controller.getCtx();
  }

  @Test
  @Order(1)
  public void testGetInputValues() {

    List<Expr<IntSort>> inputExpr = new ArrayList<>();
    List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>> expectedResult = new ArrayList<>();

    for (int i = 0; i < 2; i++) {
      inputExpr.add(TestController.getCtx()
        .mkConst("input_" + i, TestController.getCtx().getIntSort()));
      InPort<AnnotatedValue<Expr<IntSort>, Integer>> in = new InPort<>();
      in.update(AnnotatedValue.newAnnoValue(inputExpr.get(i), 0));
      expectedResult.add(in);
    }

    Solver solver = ctx.mkSolver();
    solver.check();

    List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>> result =
      DSE.getInputValues(solver.getModel(), inputExpr);

    for (int i = 0; i < 2; i++) {
      int finalI = i;
      assertAll(
        () -> assertThat(result.get(finalI).getValue()
          .getExpr()).isEqualTo(expectedResult.get(finalI).getValue().getExpr()),
        () -> assertThat(result.get(finalI).getValue()
          .getValue()).isEqualTo(expectedResult.get(finalI).getValue().getValue())
      );
    }
  }

  @Test
  @Order(2)
  public void testRunOnce() {

    List<Expr<IntSort>> inputExpr = new ArrayList<>();
    List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>> inputList = new ArrayList<>();

    List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>> expectedResult = new ArrayList<>();

    for (int i = 0; i < 2; i++) {
      inputExpr.add(TestController.getCtx()
        .mkConst("input_" + i, TestController.getCtx().getIntSort()));

      InPort<AnnotatedValue<Expr<IntSort>, Integer>> in = new InPort<>();
      in.update(AnnotatedValue.newAnnoValue(inputExpr.get(i), i));
      inputList.add(in);
    }

    Expr constInput = TestController.getCtx()
      .mkConst("input_0", TestController.getCtx().getIntSort());
    Expr constInput1 = TestController.getCtx()
      .mkConst("input_1", TestController.getCtx().getIntSort());

    OutPort<AnnotatedValue<Expr<IntSort>, Integer>> portOutput = new OutPort<>();
    portOutput.setValue(AnnotatedValue.newAnnoValue(constInput, 0));

    OutPort<AnnotatedValue<Expr<IntSort>, Integer>> portOutput2 = new OutPort<>();
    portOutput2.setValue(AnnotatedValue.newAnnoValue(constInput1, 1));

    expectedResult.add(portOutput);
    expectedResult.add(portOutput2);

    List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>> result = DSE.runOnce(inputList);

    for (int i = 0; i < 2; i++) {
      int finalI = i;

      assertAll(
        () -> assertThat(result.get(finalI).getValue().getExpr()
          .toString()).isEqualTo(expectedResult.get(finalI).getValue().getExpr().toString()),
        () -> assertThat(result.get(finalI).getValue()
          .getValue()).isEqualTo(expectedResult.get(finalI).getValue().getValue())
      );
    }
  }
}
