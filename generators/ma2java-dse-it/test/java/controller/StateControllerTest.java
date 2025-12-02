/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.PathCondition;
import montiarc.rte.dse.StateInfo;
import montiarc.rte.dse.StatesList;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StateControllerTest {

  StateController
          <List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>>,
                  List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>>> controller;

  @BeforeEach
  void setUpMock() {
    controller = new StateController<>();
    assertThat(controller).isNotNull();

    controller.init();

    assertThat(controller).isNotNull();
  }

  @Test
  public void testSaveStates() {
    StateInfo stateInfo = StateInfo.newStateInfo(TestEnum1.HAMBURG,
            Arrays.asList("country : Germany"), "testComponent");
    StatesList statesList = new StatesList(Arrays.asList(stateInfo));

    controller.saveStates(statesList);

    assertThat(controller.getCurrentState()).isEqualTo(statesList);
  }

  @Test
  public void testShouldEndRun() {

    StateInfo stateInfoHamburg = StateInfo.newStateInfo(TestEnum1.HAMBURG,
            Arrays.asList("country : Germany"), "testComponent1");
    StateInfo stateInfoCSRwth = StateInfo.newStateInfo(TestEnum2.COMPUTERSCIENCE,
            Arrays.asList("university : RWTH"), "testComponent2");
    StateInfo stateInfoCSHamburg = StateInfo.newStateInfo(TestEnum2.COMPUTERSCIENCE,
            Arrays.asList("university : Hamburg"), "testComponent2");

    StatesList statesList = new StatesList(Arrays.asList(stateInfoHamburg, stateInfoCSRwth));

    //add the state 11 times to reach upper limit
    for (int i = 0; i < 10; i++) {
      controller.saveStates(statesList);
      controller.shouldEndRun();
    }

    assertThat(controller.shouldEndRun()).isEqualTo(true);
    assertThat(controller.getVisitedStates().contains(statesList));

    StatesList statesListCSRwth = new StatesList(Arrays.asList(stateInfoCSRwth));

    controller.saveStates(statesListCSRwth);

    assertThat(controller.shouldEndRun()).isEqualTo(false);
    assertThat(controller.getVisitedStates().contains(statesListCSRwth));

    StatesList statesListCSHamburg = new StatesList(Arrays.asList(stateInfoHamburg,
            stateInfoCSHamburg));

    controller.saveStates(statesListCSHamburg);

    assertThat(controller.shouldEndRun()).isEqualTo(false);
    assertThat(controller.getVisitedStates().contains(statesListCSHamburg));

  }

  @Test
  public void testCompareStates() {
    StateInfo stateInfoCSRwth = StateInfo.newStateInfo(TestEnum2.COMPUTERSCIENCE,
            Arrays.asList("university : RWTH"), "testComponent2");
    StateInfo stateInfoCSHamburg = StateInfo.newStateInfo(TestEnum2.COMPUTERSCIENCE,
            Arrays.asList("university : Hamburg"), "testComponent2");
    StateInfo stateInfoHamburg = StateInfo.newStateInfo(TestEnum1.HAMBURG,
            Arrays.asList("country : Germany"), "testComponent1");

    StatesList visitedStatesList = new StatesList(Arrays.asList(stateInfoCSRwth));

    StatesList currentStatesList = new StatesList(Arrays.asList(stateInfoCSHamburg));

    assertThat(controller.compareStates(
            Set.of(Pair.of(visitedStatesList, 1), Pair.of(currentStatesList, 3)), currentStatesList))
            .isEqualTo(Pair.of(currentStatesList, 3));
    assertThat(controller.compareStates(Set.of(Pair.of(visitedStatesList, 1)),
            StatesList.newStatesList(Arrays.asList(stateInfoHamburg))))
            .isNull();
  }

  @Test
  public void testAddBranches() {
    BoolExpr expr = controller.getCtx().mkBool(true);

    controller.addBranch(expr, "branch1");
    assertThat(controller.currentBranches).isNotNull();
    assertThat(controller.currentBranches.getBranchConditions()).isEqualTo(controller.getCtx().mkAnd(expr, expr));
    assertThat(controller.currentBranches.getBranchIds()).isEqualTo(List.of("branch1"));
  }

  @Test
  public void testSaveInformation() {
    // case: aborted = true
    controller.currentBranches = new PathCondition();

    controller.aborted = true;
    controller.abortConditions = new HashSet<>();
    controller.abortConditions.add(controller.getCtx().mkBool(false));
    controller.saveInformation();

    assertThat(controller.abortConditions.contains(controller.getCtx().mkNot(controller.currentBranches.getBranchConditions())));

    controller.aborted = false;

    StateInfo stateInfoCSRwth = StateInfo.newStateInfo(TestEnum2.COMPUTERSCIENCE,
            Arrays.asList("university : RWTH"), "testComponent2");
    StateInfo stateInfoCSHamburg = StateInfo.newStateInfo(TestEnum2.COMPUTERSCIENCE,
            Arrays.asList("university : Hamburg"), "testComponent2");
    StateInfo stateInfoHamburg = StateInfo.newStateInfo(TestEnum1.HAMBURG,
            Arrays.asList("country : Germany"), "testComponent1");

    StatesList visitedStatesList = new StatesList(Arrays.asList(stateInfoCSRwth));

    StatesList currentStatesList = new StatesList(Arrays.asList(stateInfoCSHamburg));

    // visited states in the current computation
    controller.visitedStates.add(Pair.of(visitedStatesList, 1));
    controller.visitedStates.add(Pair.of(currentStatesList, 2));
    controller.currentBranches = new PathCondition();

    // visited states in the complete dse run
    controller.visitedStatesAll.add(Pair.of(visitedStatesList, 3));

    controller.saveInformation();

    assertThat(controller.currentBranches.getBranchIds().size()).isEqualTo(0);
    assertThat(controller.visitedStates.size()).isEqualTo(0);

    assertThat(controller.visitedStatesAll.size()).isEqualTo(2);
    assertThat(controller.visitedStatesAll.contains(Pair.of(visitedStatesList, 4))).isTrue();
    assertThat(controller.visitedStatesAll.contains(Pair.of(currentStatesList, 2))).isTrue();

  }

  private enum TestEnum1 {
    HAMBURG;
  }

  private enum TestEnum2 {
    COMPUTERSCIENCE;
  }
}
