/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.rte.automaton.State;

public class AssertEqualsAutomaton<T> extends AssertEqualsAutomatonTOP<T> {

  protected AssertEqualsAutomaton(AssertEqualsContext<T> assertEqualsContext, AssertEqualsStates<T> states, State initial, String name) {
    super(assertEqualsContext, states, initial, name);
    transition_msg_actual_1 = new montiarc.rte.automaton.TransitionBuilder<T>()
      .setSource(states.state_S)
      .setTarget(states.state_S)
      .setGuard((actual) -> true)
      .setAction(
        (actual) -> {
          T expected = context.param_expected();
          java.lang.String message = context.param_message();

          // Override assertEquals since == compares object hashes (not desired for Wrapped primitives and Strings)
          // Cannot do this directly in MontiArc since there T does not inherit from Object
          Assertions.assertEquals(expected, actual, message);

          this.states.state_S.doAction();
        })
      .build();
  }
}
