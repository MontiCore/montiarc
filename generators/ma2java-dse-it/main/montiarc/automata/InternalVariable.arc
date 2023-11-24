/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Simple atomic component with one internal variable. The output is the current state of the internal variable.
 */
component InternalVariable {
  port in Integer in;
  port out Integer out;

  Integer intern = 0;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle /{
      intern = intern + 1;
      out = intern;
    };
  }
}
