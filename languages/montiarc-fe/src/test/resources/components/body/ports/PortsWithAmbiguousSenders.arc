package components.body.ports;

import components.body.subcomponents._subcomponents.HasGenericInputAndOutputPort;

/*
 * Invalid model.
 * Ports have more than one incoming transition.
 * Produces 4 errors, as specified below.
 *
 * Formerly named "R8" in MontiArc3.
 *
 * @implements [Hab16] R1: Each outgoing port of a component type definition
                             is used at most once as target of a connector.
                             (p. 63, Lst. 3.36)
 * @implements [Hab16] R2: Each incoming port of a subcomponent is used at
                             most once as target of a connector.
                             (p. 62, Lst. 3.37)
 */
component PortsWithAmbiguousSenders {
  port
    in String in1,
    in String in2, 
    out Integer out1;
    
  component HasGenericInputAndOutputPort<String> p1;
  component HasGenericInputAndOutputPort<Integer> p2;
  component HasGenericInputAndOutputPort<Integer> p3;
  component HasGenericInputAndOutputPort<Integer> p4;
  
  // correct
  connect in1 -> p1.tIn;
  connect p2.tOut -> out1;
  connect p2.tOut -> p4.tIn;

  connect in2 -> p1.tIn; // ERROR: More than one incoming connector for port p1.tIn
  connect p3.tOut -> out1; // ERROR: More than one incoming connector for port out1
  connect p4.tOut -> out1; // ERROR: More than one incoming connector for port out1
  connect p3.tOut -> p4.tIn; // ERROR: More than one incoming connector for port p4.tIn
}