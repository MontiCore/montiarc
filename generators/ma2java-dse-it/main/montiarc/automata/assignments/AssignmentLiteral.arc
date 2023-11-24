/* (c) https://github.com/MontiCore/monticore */
package assignments;

/**
 * Simple atomic component where the output is assigned a literal
 */
component AssignmentLiteral {
  port in Integer in;
  port out Integer out;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle /{
      out = 1;
    };
  }
}
