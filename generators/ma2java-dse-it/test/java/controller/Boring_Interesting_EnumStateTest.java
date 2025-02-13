/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import controller.Boring_Interesting_EnumState_SM;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.dse.StateInfo;
import montiarc.rte.dse.StatesList;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// This class test the Boring_Interesting_EnumState Controller using Boring_Interesting_EnumState_SM as an instance
public class Boring_Interesting_EnumStateTest {

  Boring_Interesting_EnumState_SM<List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>>, List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>>> controller;

  @BeforeEach
  void setUpMock() {
    controller = new Boring_Interesting_EnumState_SM<>();
    assertThat(controller).isNotNull();

    controller.init();

    assertThat(controller).isNotNull();
  }

  @Test
  public void testInit() {
    assertThat(controller.boring.toString()).isEqualTo(Arrays.asList("" +
            "nonModuleevaluation").toString());
    assertThat(controller.interesting.toString()).isEqualTo(Arrays.asList(
            "IdlecounterSA",
            "IdlecounterMDSE").toString());
  }

  @Test
  public void testGetMaxNum() {
    StateInfo stateBoring = StateInfo.newStateInfo(TestEnum.non,
            Arrays.asList("internalState"), "Moduleevaluation");
    StateInfo stateInteresting = StateInfo.newStateInfo(TestEnum.Idle,
            Arrays.asList("internalState"), "counterSA");
    StateInfo stateNormal = StateInfo.newStateInfo(TestEnum.enumState1,
            Arrays.asList("internalState"), "testComponent1");

    StatesList boring = new StatesList(Arrays.asList(stateBoring));
    StatesList interesting = new StatesList(Arrays.asList(stateInteresting));
    StatesList normal = new StatesList(Arrays.asList(stateNormal));

    assertThat(controller.getMaxNum(boring)).isEqualTo(controller.boringCount);
    assertThat(controller.getMaxNum(interesting)).isEqualTo(-1);
    assertThat(controller.getMaxNum(normal)).isEqualTo(controller.normalCount);
  }


  @Test
  public void testShouldEndRun() {
    StateInfo stateBoring = StateInfo.newStateInfo(TestEnum.non,
            Arrays.asList("internalState"), "Moduleevaluation");
    StateInfo stateInteresting = StateInfo.newStateInfo(TestEnum.Idle,
            Arrays.asList("internalState"), "counterSA");
    StateInfo stateNormal = StateInfo.newStateInfo(TestEnum.enumState1,
            Arrays.asList("internalState"), "testComponent1");

    StatesList boring = new StatesList(Arrays.asList(stateBoring));
    StatesList interesting = new StatesList(Arrays.asList(stateInteresting));
    StatesList normal = new StatesList(Arrays.asList(stateNormal));

    //case: interesting
    controller.currentState = interesting;

    assertThat(controller.shouldEndRun()).isEqualTo(false);
    assertThat(controller.visitedStates.contains(Pair.of(controller.currentState, 1))).isTrue();

    assertThat(controller.shouldEndRun()).isEqualTo(false);
    assertThat(controller.visitedStates.contains((Pair.of(controller.currentState, 2)))).isTrue();


    //case: boring
    controller.currentState = boring;
    assertThat(controller.shouldEndRun()).isEqualTo(false);
    assertThat(controller.visitedStates.contains(Pair.of(controller.currentState, 1))).isTrue();

    assertThat(controller.shouldEndRun()).isEqualTo(true);
    assertThat(controller.aborted).isEqualTo(true);
    controller.aborted = false;

    controller.visitedStatesAll.add(Pair.of(controller.currentState, 2));
    assertThat(controller.shouldEndRun()).isEqualTo(true);
    assertThat(controller.aborted).isEqualTo(true);
    controller.aborted = false;

    //case: normal
    controller.currentState = normal;
    controller.visitedStates.add(Pair.of(controller.currentState, 9));
    assertThat(controller.shouldEndRun()).isEqualTo(false);
    assertThat(controller.shouldEndRun()).isEqualTo(true);

    controller.aborted = false;
    controller.visitedStates.clear();
    controller.visitedStatesAll.add(Pair.of(controller.currentState, 10));
    assertThat(controller.shouldEndRun()).isEqualTo(true);
  }

  private enum TestEnum {
    non,
    Idle,
    enumState1;
  }
}