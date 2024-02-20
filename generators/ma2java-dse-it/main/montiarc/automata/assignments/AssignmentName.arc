/* (c) https://github.com/MontiCore/monticore */
package automata.assignments;

/**
 * Simple atomic component where the output is assigned a variable
 */
component AssignmentName {
  port in Integer in;
  port out Integer out;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle /{
      out = in;
    };
  }
}
