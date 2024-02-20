/* (c) https://github.com/MontiCore/monticore */
package automata.dataTypes;

/**
 * Simple atomic component for testing doubles
 */
component DoubleComponent {
  port in Double in;
  port out Double out;

  Double doubler = 2.4;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle [in*2 == 4.2]/{
      out = in + doubler;
    };

    Idle -> Idle /{
      out = 1.0;
    };
  }
}
