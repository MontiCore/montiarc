/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TransitionsControllerTest {

  TransitionsController
    <List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>>,
      List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>>> controller;

  @BeforeEach
  void setUpMock() {
    controller = new TransitionsController<>();
    assertThat(controller).isNotNull();

    controller.init();

    assertThat(controller).isNotNull();
  }

  @Test
  public void testAddBranch() {

    BoolExpr expr1 = controller.getCtx().mkLe(controller.getCtx().mkInt(42),
      controller.getCtx().mkInt(43));
    String branchId1 = "testBranch1";

    BoolExpr expr2 = controller.getCtx().mkGe(controller.getCtx().mkInt(42),
      controller.getCtx().mkInt(43));
    String branchId2 = "testBranch2";

    controller.addBranch(expr1, branchId1);
    controller.addBranch(expr2, branchId2);

    Pair<BoolExpr, String> branch1 = ImmutablePair.of(expr1, branchId1);
    Pair<BoolExpr, String> branch2 = ImmutablePair.of(expr2, branchId2);

    assertThat(controller.getCurrentTransitions()).isEqualTo(Arrays.asList(branch1, branch2));
  }

  @Test
  public void testShouldEndRun() {

    BoolExpr expr1 = controller.getCtx().mkLe(controller.getCtx().mkInt(42),
      controller.getCtx().mkInt(43));
    String branchId1 = "testBranch1";

    BoolExpr expr2 = controller.getCtx().mkGe(controller.getCtx().mkInt(42),
      controller.getCtx().mkInt(43));
    String branchId2 = "testBranch2";

    BoolExpr expr3 = controller.getCtx().mkLt(controller.getCtx().mkInt(42),
      controller.getCtx().mkInt(43));
    String branchId3 = "testBranch3";

    BoolExpr expr4 = controller.getCtx().mkLt(controller.getCtx().mkInt(42),
      controller.getCtx().mkInt(43));
    String branchId4 = "testBranch4";

    BoolExpr expr5 = controller.getCtx().mkLt(controller.getCtx().mkInt(42),
      controller.getCtx().mkInt(43));
    String branchId5 = "testBranch5";

    BoolExpr expr6 = controller.getCtx().mkLt(controller.getCtx().mkInt(42),
      controller.getCtx().mkInt(43));
    String branchId6 = "testBranch6";


    controller.addBranch(expr1, branchId1);
    controller.addBranch(expr2, branchId2);

    assertThat(controller.shouldEndRun()).isEqualTo(false);

    for (int i = 0; i < 10; i++) {
      controller.addBranch(expr3, branchId3);
      controller.addBranch(expr4, branchId4);

      assertThat(controller.shouldEndRun()).isEqualTo(false);
    }

    controller.addBranch(expr1, branchId1);
    controller.addBranch(expr3, branchId3);

    assertThat(controller.shouldEndRun()).isEqualTo(true);

    controller.saveInformation();

    List<Pair<String, Integer>> expectedTransitionsAll = new ArrayList<>();

    expectedTransitionsAll.add(MutablePair.of(branchId1, 2));
    expectedTransitionsAll.add(MutablePair.of(branchId2, 1));
    expectedTransitionsAll.add(MutablePair.of(branchId4, 4));
    expectedTransitionsAll.add(MutablePair.of(branchId3, 5));

    Set<BoolExpr> expectedAbortConditions = new HashSet<>();
    expectedAbortConditions.add(controller.getCtx().mkNot(expr3));

    assertThat(controller.getTransitionsAll().containsAll(expectedTransitionsAll));
    assertThat(controller.getAbortConditions()).isEqualTo(expectedAbortConditions);
  }
}
