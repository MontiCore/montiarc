/* (c) https://github.com/MontiCore/monticore */
package assignments;

/**
 * Simple atomic component where the output is assigned a variable
 */
component AssignmentName {
  port <<sync>> in Integer in;
  port <<sync>> out Integer out;

  automaton{
    initial state Idle;

    Idle -> Idle /{
      out = in;
    };
  }
}
