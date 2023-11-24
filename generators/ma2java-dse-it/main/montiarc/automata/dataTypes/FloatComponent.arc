/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

/**
 *  Simple atomic component for testing floats
 */
component FloatComponent {
  port in Float in;
  port out Float out;

  Float floater = 42.4f;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle [in /2 == 3.2f]/{
      out = in + floater;
    };

    Idle -> Idle /{
      out = 1.0f;
    };
  }
}
