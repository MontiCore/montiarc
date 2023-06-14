/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

/**
 *  Simple atomic component for testing doubles
 */
component DoubleComponentParameter(Double parameter) {
  port <<sync>> in double in;
  port <<sync>> out double out;

  Double doubler = 10.2;

  automaton{
    initial state Idle;

    Idle -> Idle [in /2 == 3.2]/{
      out = in + doubler;
    };

    Idle -> Idle /{
      out = parameter;
    };
  }
}
