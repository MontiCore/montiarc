/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

/**
 *  Simple atomic component for testing floats
 */
component FloatComponent {
  port <<sync>> in float in;
  port <<sync>> out Float out;

  Float floater = 42.4f;

  automaton{
    initial state Idle;

    Idle -> Idle [in /2 == 3.2f]/{
      out = in + floater;
    };

    Idle -> Idle /{
      out = 1.0f;
    };
  }
}
