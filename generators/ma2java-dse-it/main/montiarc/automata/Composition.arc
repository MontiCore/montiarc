/* (c) https://github.com/MontiCore/monticore */
package automata;

import assignments.*;

/**
 * Simple composite component where the input of the component is transferred directly to the output.
 */
component Composition {
  port <<sync>> in Integer in;
  port <<sync>> out Integer out;

  AssignmentName comp1;
  AssignmentName comp2;

  in -> comp1.in;
  comp1.out -> comp2.in;
  comp2.out -> out;
}
