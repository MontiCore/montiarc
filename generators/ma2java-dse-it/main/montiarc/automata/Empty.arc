/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Empty atomic component, the component should not produce any output.
 */
component Empty {
  port <<sync>> in Integer in;
  port <<sync>> out Integer out;
}
