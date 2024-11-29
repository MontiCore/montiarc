/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.StateInfo;
import montiarc.rte.dse.StatesList;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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


  private enum TestEnum1 {
    HAMBURG;
  }

  private enum TestEnum2 {
    COMPUTERSCIENCE;
  }
}
