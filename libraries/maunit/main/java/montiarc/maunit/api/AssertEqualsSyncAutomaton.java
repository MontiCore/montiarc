/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import montiarc.rte.automaton.State;
import montiarc.rte.automaton.TransitionBuilder;

import java.util.List;

public class AssertEqualsSyncAutomaton<T> extends AssertEqualsSyncAutomatonTOP<T> {

  protected AssertEqualsSyncAutomaton(AssertEqualsSyncContext<T> assertEqualsSyncContext, AssertEqualsSyncStates<T> states, State initial, String name) {
    super(assertEqualsSyncContext, states, initial, name);
    this.transitions.clear();
    this.transitions.add(
      new TransitionBuilder<AssertEqualsSyncSyncMsg<T>>()
        .setSource(states.state_S)
        .setTarget(states.state_S)
        .setGuard((AssertEqualsSyncMsgGuard<T>) (actual) -> true)
        .setAction(
          (AssertEqualsSyncMsgAction<T>)
            (actual) -> {
              List<T> expected = context.param_expected();
              String message = context.param_message();
              int index = context.field_index();

              if (index >= expected.size()) {
                Assertions.fail(
                  "Unexpected additional message received with value: " + actual);
              }
              Assertions.assertEquals(expected.get(index), actual, message);
              index++;

              context.set_field_index(index);
              this.states.state_S.doAction();
            })
        .build());
  }
}
