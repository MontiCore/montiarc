/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.Expr;
import com.microsoft.z3.IntSort;
import montiarc.rte.dse.AnnotatedValue;
import montiarc.rte.timesync.IInPort;
import montiarc.rte.timesync.IOutPort;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// This class test the Boring_Interesting_Transition Controller using Boring_Interesting_Transition_SM as an instance
public class Boring_Interesting_TransitionTest {

  Boring_Interesting_Transitions_SM<List<IInPort<AnnotatedValue<Expr<IntSort>, Integer>>>, List<IOutPort<AnnotatedValue<Expr<IntSort>, Integer>>>> controller;

  @BeforeEach
  void setUpMock() {
    controller = new Boring_Interesting_Transitions_SM<>();
    assertThat(controller).isNotNull();

    controller.init();

    assertThat(controller).isNotNull();
  }

  @Test
  public void testInit() {
    assertThat(controller.boring.toString()).isEqualTo(Arrays.asList(
            "evaluationFrommdseTononModule2",
            "evaluationFromsaTononModule2",
            "evaluationFromnonModuleTononModule2").toString());
    assertThat(controller.interesting.toString()).isEqualTo(Arrays.asList(
            "distinctionFromIdleToIdle1").toString());
  }

  @Test
  public void testGetMaxNum() {
    assertThat(controller.getMaxNum("evaluationFrommdseTononModule2")).isEqualTo(controller.boringCount);
    assertThat(controller.getMaxNum("distinctionFromIdleToIdle1")).isEqualTo(-1);
    assertThat(controller.getMaxNum("branch1")).isEqualTo(controller.normalCount);
  }

  @Test
  public void testShouldEndRun() {
    controller.currentTransitions.add(Pair.of(controller.getCtx().mkBool(true),
            "distinctionFromIdleToIdle1"));
    assertThat(controller.shouldEndRun()).isEqualTo(false);
    assertThat(controller.currentTransitions.size()).isEqualTo(0);
    assertThat(controller.aborted).isEqualTo(false);

    controller.currentTransitions.add(Pair.of(controller.getCtx().mkBool(true),
            "evaluationFrommdseTononModule2"));

    assertThat(controller.shouldEndRun()).isEqualTo(false);

    controller.currentTransitions.clear();
    controller.currentTransitions.add(Pair.of(controller.getCtx().mkBool(true),
            "branch1"));

    assertThat(controller.shouldEndRun()).isEqualTo(false);

  }

  @Test
  public void testCountTransitions() {

    //case: boring transitions
    controller.currentTransitions.add(
            Pair.of(controller.getCtx().mkBool(false), "evaluationFrommdseTononModule2"));

    controller.currentCountedTransitions.add(Pair.of("evaluationFrommdseTononModule2", 1));
    assertThat(controller.countTransition()).isEqualTo(true);

    controller.currentTransitions.add(
            Pair.of(controller.getCtx().mkBool(false), "evaluationFrommdseTononModule2"));
    controller.aborted = false;
    controller.transitionsAll.add(Pair.of("evaluationFrommdseTononModule2", 1));
    assertThat(controller.countTransition()).isEqualTo(true);

    controller.currentTransitions.add(
            Pair.of(controller.getCtx().mkBool(false), "evaluationFrommdseTononModule2"));
    controller.aborted = false;
    controller.currentCountedTransitions.clear();
    assertThat(controller.countTransition()).isEqualTo(true);
    controller.aborted = false;

    // case: normal transitions
    controller.currentTransitions.add(Pair.of(controller.getCtx().mkBool(false), "transition"));

    controller.currentCountedTransitions.add(Pair.of("transition", 1));
    assertThat(controller.countTransition()).isEqualTo(false);

    controller.currentTransitions.add(Pair.of(controller.getCtx().mkBool(false), "transition"));
    controller.transitionsAll.add(Pair.of("transition", 9));
    assertThat(controller.countTransition()).isEqualTo(true);

    controller.currentTransitions.add(Pair.of(controller.getCtx().mkBool(false), "transition"));
    controller.aborted = false;
    controller.currentCountedTransitions.clear();
    assertThat(controller.countTransition()).isEqualTo(false);

  }
}
