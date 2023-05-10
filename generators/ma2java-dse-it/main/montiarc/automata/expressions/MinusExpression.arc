/* (c) https://github.com/MontiCore/monticore */
package expressions;

/**
 * Simple atomic component, the output is the input subtracted by one
 */
component MinusExpression {
  port <<sync>> in Integer in;
  port <<sync>> out Integer out;

  automaton{
    initial state Idle;

    Idle -> Idle /{
      out = in - 1;
    };
  }
}
