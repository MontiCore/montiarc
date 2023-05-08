/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Atomic component with a parameter.
 */
component Parameter (Integer parameter) {
  port <<sync>> in Integer inPort,
       <<sync>> out Integer out;

  automaton{
    initial state Idle;

    Idle -> Idle /{
        out = parameter;
    };
  }
}
