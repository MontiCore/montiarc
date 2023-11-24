/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Atomic component with one parameter that is passed directly to the output.
 */

component Parameter (Integer parameter) {
  port in Integer in;
  port out Integer out;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle /{
        out = parameter;
    };
  }
}
