/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;
import java.lang.Boolean;

/*
 * Invalid model
 *
 * Formerly named "R5" in MontiArc3.
 *
 * @implements [Hab16] R6: The second part of a qualified connector’s source
 *                          respectively target must correspond to a port
 *                          name of the referenced subcomponent determined
 *                          by the first part. (p.64 Lst. 3.41)
 */
component ConnectsNonExistingPorts2 {
  
  port
    in String portIn,
    out Boolean somePort,
    out String portOut;
  
  component components.body.subcomponents._subcomponents.HasStringInputAndIntegerOutput p1;
  component components.body.subcomponents._subcomponents.HasStringInputAndIntegerOutput p2;
  component components.body.subcomponents._subcomponents.IntegerInputAndBooleanOutput p3;
  
  // correct
  connect portIn -> p1.portIn, p2.portIn;
  connect p1.portOut -> p3.integer;
  
  // port does not exist
  connect p1.notExists -> p3.inNotExists;
      //ERROR: The source port 'p1.notExists' does not exist!
      //ERROR: The target port 'p3.inNotExists' does not exist!
  connect p3.outNotExists -> somePort;
      //ERROR: The source port 'p3.outNotExists' does not exist!

}
