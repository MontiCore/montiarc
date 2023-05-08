/* (c) https://github.com/MontiCore/monticore */
package automata;

/**
 * Simple composite component where the input of the component is transferred directly to the output.
 */
component Composition {
  port <<sync>> in Integer in;
  port <<sync>> out Integer out;

  Simple comp1;
  Simple comp2;

  in -> comp1.in;
  comp1.out -> comp2.in;
  comp2.out -> out;
}
