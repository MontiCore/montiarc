/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import de.monticore.rte.streams.UntimedStream;
import montiarc.rte.automaton.State;

public class AssertEqualsUntimedAutomaton<T> extends AssertEqualsUntimedAutomatonTOP<T> {

  protected AssertEqualsUntimedAutomaton(AssertEqualsUntimedContext<T> assertEqualsUntimedContext, AssertEqualsUntimedStates<T> states, State initial, String name) {
    super(assertEqualsUntimedContext, states, initial, name);

    transition_msg_actual_1 =
      new montiarc.rte.automaton.TransitionBuilder<T>()
        .setSource(states.state_S)
        .setTarget(states.state_S)
        .setGuard((actual) -> true)
        .setAction(
          (actual) -> {
            this.states.state_S.exitSub(state);

            de.monticore.rte.streams.UntimedStream<T> expected = context.param_expected();
            java.lang.String message = context.param_message();

            de.monticore.rte.streams.UntimedStream<T> remaining = context.field_remaining();

            if (remaining.isEmpty()) {
              montiarc.maunit.api.Assertions
                .fail("Unexpected additional message received with value: " + actual);
            }
            montiarc.maunit.api.Assertions.assertEquals(remaining.first(), actual, message);
            remaining = remaining.dropFirst();

            if (remaining != null) context.set_field_remaining(remaining);

            this.states.state_S.enterWithSub();
          })
        .build();
  }
}
