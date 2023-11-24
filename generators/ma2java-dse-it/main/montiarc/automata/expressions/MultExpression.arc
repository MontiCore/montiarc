/* (c) https://github.com/MontiCore/monticore */
package expressions;

/**
 * Simple atomic component, the output is the input multiplied by two
 */
component MultExpression {
  port in Integer in;
  port out Integer out;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle /{
      out = in * 2;
    };
  }
}
