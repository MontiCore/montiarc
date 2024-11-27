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
          java.util.List<java.util.List<T>> expected = context.param_expected();
          java.lang.String message = context.param_message();

          int tick = context.field_tick();
          int index = context.field_index();

          if (tick >= expected.size() || index >= expected.get(tick).size()) {
            Assertions.fail(
              "Unexpected additional message received in tick: "
                + tick
                + " with value: "
                + actual);
          }

          // Override assertEquals since == compares object hashes (not desired for Wrapped primitives and Strings)
          // Cannot do this directly in MontiArc since there T does not inherit from Object
          Assertions.assertEquals(
            expected.get(tick).get(index), actual, message);
          index++;

          context.set_field_tick(tick);
          context.set_field_index(index);
          this.states.state_S.doAction();
        })
      .build();
  }
}
