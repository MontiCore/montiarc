package components.body.automaton.transition.assignments;


component AssignmentToIncomingPort {

  port in Integer inInteger;

  automaton AssignmentToIncomingPort {
    state Initial, State2;

    initial Initial / {inInteger = 2};
    Initial -> State2 / {inInteger = 5};
  }
}