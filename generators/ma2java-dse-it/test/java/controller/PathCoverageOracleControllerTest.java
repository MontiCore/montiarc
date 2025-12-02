/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import com.microsoft.z3.Solver;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PathCoverageOracleControllerTest {

  PathCoverageOracleController
          <List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>>,
                  List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>>> controller;

  @BeforeEach
  void setUpMock() {
    controller = new PathCoverageOracleController<>();
    assertThat(controller).isNotNull();

    controller.init();

    assertThat(controller).isNotNull();
  }

  @Test
  public void testGetIfOracles() {

    boolean oracle1 = controller.getIfOracle("testBranch1");
    boolean oracle2 = controller.getIfOracle("testBranch2");
    boolean oracle3 = controller.getIfOracle("testBranch3");

    assertThat(controller.getRunOracles()).isEqualTo(Arrays.asList(oracle1, oracle2, oracle3));
  }

  @Test
  public void testLoadBoolListValue() {
    boolean oracle1 = controller.getIfOracle("testBranch1");
    boolean oracle2 = controller.getIfOracle("testBranch2");
    boolean oracle3 = controller.getIfOracle("testBranch3");

    Solver s = controller.getCtx().mkSolver();
    s.check();
    List<BoolExpr> boolExprs = new ArrayList<>();
    assertThat(controller.loadBoolListValue(s.getModel(), boolExprs))
            .isEqualTo(Arrays.asList(!oracle1, !oracle2, !oracle3));

    boolean oracle4 = controller.getIfOracle("testBranch4");
    boolean oracle5 = controller.getIfOracle("testBranch5");
    boolean oracle6 = controller.getIfOracle("testBranch6");
    boolean oracle7 = controller.getIfOracle("testBranch7");

    assertThat(controller.loadBoolListValue(s.getModel(), boolExprs))
            .isEqualTo(Arrays.asList(!oracle4, !oracle5, !oracle6, !oracle7));

  }
}
