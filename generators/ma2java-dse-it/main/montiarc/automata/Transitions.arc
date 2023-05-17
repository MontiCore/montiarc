/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Atomic component with multiple transitions
 */
component Transitions {
  port <<sync>> in Integer in;
  port <<sync>> out Integer out;

  Integer intern = 0;

  automaton{
    initial state Idle;
    state Second;

    Idle -> Idle/{
      out = 100;
    };

    Idle -> Second [in > 2] /{
      out = in+1;
    };

    Second -> Idle [in == 100] /{
      out = in*2;
    };
  }
}
