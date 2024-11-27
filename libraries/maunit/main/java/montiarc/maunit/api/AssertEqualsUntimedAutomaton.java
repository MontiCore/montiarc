/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.rte.automaton.State;

public class AssertEqualsUntimedAutomaton<T> extends AssertEqualsUntimedAutomatonTOP<T> {

  protected AssertEqualsUntimedAutomaton(AssertEqualsUntimedContext<T> assertEqualsUntimedContext, AssertEqualsUntimedStates<T> states, State initial, String name) {
    super(assertEqualsUntimedContext, states, initial, name);

    transition_msg_actual_1 = new montiarc.rte.automaton.TransitionBuilder<T>()
      .setSource(states.state_S)
      .setTarget(states.state_S)
      .setGuard((actual) -> true)
      .setAction(
        (actual) -> {
          java.util.List<T> expected = context.param_expected();
          java.lang.String message = context.param_message();

          int index = context.field_index();

          if (index >= expected.size()) {
            Assertions.fail(
              "Unexpected additional message received with value: " + actual);
          }

          // Override assertEquals since == compares object hashes (not desired for Wrapped primitives and Strings)
          // Cannot do this directly in MontiArc since there T does not inherit from Object
          Assertions.assertEquals(expected.get(index), actual, message);
          index++;

          context.set_field_index(index);
          this.states.state_S.doAction();
        })
      .build();
  }
}
