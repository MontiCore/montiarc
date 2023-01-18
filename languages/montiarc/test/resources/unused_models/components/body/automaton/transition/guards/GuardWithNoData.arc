/* (c) https://github.com/MontiCore/monticore */
package components.body.automaton.transition.guards;
/*
 * Valid model.
 */
component GuardWithNoData {

  port
    in Integer input;

  automaton GuardIsBooleanAutomaton {
    state A,B;
    initial A;

    A -> B [input == -- ];
    B -> A [true];
  }
}
