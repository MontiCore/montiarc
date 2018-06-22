package components.body.automaton.transition.assignments;

/*
 * Invalid model.
 * Assigns values to incoming ports in transitions
 * @implements [RRW14a] T6: The direction of ports has to be respected.
 * @implements [Wor16] AR2: Inputs, outputs, and variables are used
 *    correctly. (p.103, Lst 5.20)
 */
component AssignmentToIncomingPort {

  port in Integer inInteger;

  automaton AssignmentToIncomingPort {
    state Initial, State2;

    initial Initial / {inInteger = 2};
    Initial -> State2 / {inInteger = 5};
  }
}