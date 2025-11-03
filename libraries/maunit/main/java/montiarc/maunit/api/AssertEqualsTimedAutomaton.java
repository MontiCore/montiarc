/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.rte.automaton.State;

public class AssertEqualsTimedAutomaton<T> extends AssertEqualsTimedAutomatonTOP<T> {

  protected AssertEqualsTimedAutomaton(AssertEqualsTimedContext<T> assertEqualsTimedContext, AssertEqualsTimedStates<T> states, State initial, String name) {
    super(assertEqualsTimedContext, states, initial, name);

    transition_msg_actual_1 = new montiarc.rte.automaton.TransitionBuilder<T>()
      .setSource(states.state_S)
      .setTarget(states.state_S)
      .setGuard((actual) -> true)
      .setAction(
        (actual) -> {
          this.states.state_S.exitSub(state);

          de.monticore.rte.streams.EventStream<T> expected = context.param_expected();
          java.lang.String message = context.param_message();

          de.monticore.rte.streams.UntimedStream<T> remainingTick =
            context.field_remainingTick();
          de.monticore.rte.streams.EventStream<T> remaining = context.field_remaining();

          if (remainingTick.isEmpty()) {
            montiarc.maunit.api.Assertions.fail(
              "Unexpected additional message received with value: " + actual);
          }
          // Override assertEquals since == compares object hashes (not desired for Wrapped primitives and Strings)
          // Cannot do this directly in MontiArc since there T does not inherit from Object
          montiarc.maunit.api.Assertions.assertEquals(remainingTick.first(), actual, message);
          remainingTick = remainingTick.dropFirst();

          if (remainingTick != null) context.set_field_remainingTick(remainingTick);
          if (remaining != null) context.set_field_remaining(remaining);

          this.states.state_S.enterWithSub();
        })
      .build();
  }
}
