/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

import components.body.subcomponents._subcomponents.HasGenericInputAndOutputPort;

/*
 * Invalid model.
 * Ports are not connected.
 * Produces 4 errors, as specified below.
 *
 * Formerly named "R7" in MontiArc3.
 *
 * @implements [Hab16] CV5: In decomposed components, all ports should be used in at least one connector. (p.71 Lst. 3.52)
 * @implements [Hab16] CV6: All ports of subcomponents should be used in at least one connector. (p.72 Lst. 3.53)
 */
component UnconnectedPorts2 {
  
  port 
    in String usedIn,
    in String unusedIn, // WARNING: Port unusedIn is not used in any connector
    out String usedOut,
    out String unusedOut; // WARNING: Port unusedOut is not used in any connector
  
  component HasGenericInputAndOutputPort<String> p1, p2;
  // WARNING: The incoming port 'tIn' from the reference 'p2' is not connected.
  // WARNING: The outgoing port 'tOut' from the reference 'p1' is not connected.

  connect usedIn -> p1.tIn;
  connect p2.tOut -> usedOut;
}
