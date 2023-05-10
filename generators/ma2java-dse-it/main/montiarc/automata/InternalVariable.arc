/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Simple atomic component with one internal variable. The output is the current state of the internal variable.
 */
component InternalVariable {
  port <<sync>> in Integer in;
  port <<sync>> out Integer out;

  Integer intern = 0;

  automaton{
    initial state Idle;

    Idle -> Idle /{
      intern = intern + 1;
      out = intern;
    };
  }
}
