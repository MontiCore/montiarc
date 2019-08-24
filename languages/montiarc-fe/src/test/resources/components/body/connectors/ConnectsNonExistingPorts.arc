/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;


/*
 * Invalid model.
 *
 * Formerly named "E3" in MontiArc3.
 *
 * @implements [Hab16] R6: The second part of a qualified connector’s source respectively target must correspond to a port name of the referenced subcomponent determined by the first part. (p.64 Lst. 3.41)
 */
component ConnectsNonExistingPorts {
  
  component components.body.subcomponents._subcomponents.HasStringInputAndIntegerOutput p1;
  component components.body.subcomponents._subcomponents.HasStringInputAndIntegerOutput p2;
  
  port 
    in String defIn,
    out Integer defOut;
  
  connect portIn -> p1.portIn; //ERROR: The source port 'portIn' does not exist!
  connect p1.portOut -> portOut; //ERROR: The target port 'portOut' does not exist!
  
  connect defIn -> p2.portIn;
  connect p1.portOut -> defOut;
}
