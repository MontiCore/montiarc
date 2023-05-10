/* (c) https://github.com/MontiCore/monticore */
package expressions;

/**
 * Simple atomic component, the output is the input multiplied by two
 */
component MultExpression {
  port <<sync>> in Integer in;
  port <<sync>> out Integer out;

  automaton{
    initial state Idle;

    Idle -> Idle /{
      out = in * 2;
    };
  }
}
