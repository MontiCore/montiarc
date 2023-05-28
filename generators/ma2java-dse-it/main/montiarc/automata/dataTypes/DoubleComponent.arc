/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

/**
 * Simple atomic component for testing doubles
 */
component DoubleComponent {
  port <<sync>> in Double in;
  port <<sync>> out double out;

  Double doubler = 2.4;

  automaton{
    initial state Idle;

    Idle -> Idle [in*2 == 4.2]/{
      out = in + doubler;
    };

    Idle -> Idle /{
      out = 1.0;
    };
  }
}
