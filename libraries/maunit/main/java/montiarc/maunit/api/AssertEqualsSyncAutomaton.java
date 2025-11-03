/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.rte.automaton.State;

public class AssertEqualsSyncAutomaton<T> extends AssertEqualsSyncAutomatonTOP<T> {

  protected AssertEqualsSyncAutomaton(AssertEqualsSyncContext<T> assertEqualsSyncContext, AssertEqualsSyncStates<T> states, State initial, String name) {
    super(assertEqualsSyncContext, states, initial, name);
    transition_tick_1 = new montiarc.rte.automaton.TransitionBuilder<AssertEqualsSyncSyncMsg<T>>()
      .setSource(states.state_S)
      .setTarget(states.state_S)
      .setGuard((AssertEqualsSyncMsgGuard<T>) (actual) -> true)
      .setAction(
        (AssertEqualsSyncMsgAction<T>)
          (actual) -> {
            this.states.state_S.exitSub(state);

            de.monticore.rte.streams.SyncStream<T> expected = context.param_expected();
            java.lang.String message = context.param_message();

            de.monticore.rte.streams.SyncStream<T> remaining = context.field_remaining();

            if (remaining.isEmpty()) {
              montiarc.maunit.api.Assertions.fail("Unexpected additional message");
            }
            // Override assertEquals since == compares object hashes (not desired for Wrapped primitives and Strings)
            // Cannot do this directly in MontiArc since there T does not inherit from Object
            montiarc.maunit.api.Assertions.assertEquals(remaining.first(), actual, message);
            remaining = remaining.dropFirst();

            if (remaining != null) context.set_field_remaining(remaining);

            this.states.state_S.enterWithSub();
          })
      .build();
  }
}
