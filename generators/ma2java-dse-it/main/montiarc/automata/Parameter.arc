/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Atomic component with one parameter that is passed directly to the output.
 */

component Parameter (Integer parameter) {
  port sync in Integer in;
  port sync out Integer out;

  automaton{
    initial state Idle;

    Idle -> Idle /{
        out = parameter;
    };
  }
}
