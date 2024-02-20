/* (c) https://github.com/MontiCore/monticore */
package automata.dataTypes;

/**
 *  Simple atomic component for testing doubles
 */
component DoubleComponentParameter(Double parameter) {
  port in double in;
  port out double out;

  Double doubler = 10.2;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle [in /2 == 3.2]/{
      out = in + doubler;
    };

    Idle -> Idle /{
      out = parameter;
    };
  }
}
