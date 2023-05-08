/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Simple atomic component, the input of the component is directly transferred to the output.
 */
component Simple {
  port <<sync>> in Integer in;
  port <<sync>> out Integer out;

  automaton{
    initial state Idle;

    Idle -> Idle /{
      out = in;
    };
  }
}
