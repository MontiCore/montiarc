/* (c) https://github.com/MontiCore/monticore */
package variables;

import Types.Direction;

/**
 * Composed component with an multiple output ports and multiple parameters.
 * Its behavior is defined through its subcomponents.
 */
component PSubcomponents(Direction p1, Direction p2, Direction p3) {

  port <<sync>> out Direction o1, o2, o3, o4, o5;

  PSource sub1(Direction.LEFT);
  PSource sub2(p1);
  PSource sub3(p2);
  PTransitions sub4(p2, p3, Direction.FORWARDS, Direction.BACKWARDS);

  // Outgoing channels
  sub1.o -> o1;
  sub2.o -> o2;
  sub3.o -> o3;
  sub4.o1 -> o4;
  sub4.o2 -> o5;
}
