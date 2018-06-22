package components.body.connectors;

import java.lang.String;

/*
 * Invalid model
 *
 * Formerly named "K2" in MontiArc3.
 *
 * @implements [Hab16] CO1: Connectors may not pierce through component interfaces.
                  (p. 60, Lst. 3.33)
 */
component WrongConnectors2 {

  port 
    in String strIn,
    out String strOut;
  
  component components.body.subcomponents._subcomponents
                  .HasGenericInputAndOutputPort<java.lang.String> p;
  
  connect p.tOut -> strOut;
  connect strIn -> p.tIn;
  
  connect strIn -> p.tIn.wrong;
    //ERROR: The target 'p.tIn.wrong' is invalid!
    //       Define connector targets as follows: (referenceName.)incomingPortName!

  connect p.tOut.wrong2 -> strOut;
    //ERROR: The source 'p.tOut.wrong2' is invalide!
    //       Define connector sources like this: (referenceName.)outgoingPortName!
}