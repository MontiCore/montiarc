/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

import components.body.subcomponents._subcomponents.HasGenericInputAndOutputPort;
import components.body.subcomponents._subcomponents.HasPortsOfHierarchicalCDTypes;

/*
 * Invalid model.
 * The model has connectors which are not type compatible as specified below.
 *
 * Formerly named "R6" in MontiArc3.
 *
 * @implements [Hab16] R8: The target port in a connection has to be compatible
                             to the source port, i.e., the type of the target
                             port is identical or a supertype of the source
                             port type. (p.66, Lst. 3.43)
 */
component ConnectsPortsWithIncompatibleTypes {
  port
    in Integer inInteger,
    in Integer inInteger2,
    out String outString,
    out String outString2;
  
  component components.body.subcomponents
    ._subcomponents.HasStringInputAndOutput p1;
  component components.body.subcomponents
    ._subcomponents.HasIntegerInputAndStringOutput p2;
  component components.body.subcomponents
    ._subcomponents.IntegerInputAndBooleanOutput p3;
  
  component HasPortsOfHierarchicalCDTypes p4;
  component HasPortsOfHierarchicalCDTypes p5;
  
  component HasGenericInputAndOutputPort<String> p6;
  
  component HasGenericInputAndOutputPort<String> p7;

  component HasGenericInputAndOutputPort<Integer> p8;

  component HasGenericInputAndOutputPort<Integer> p9;
  
  // ERROR: https://git.rwth-aachen.de/monticore/montiarc/core/issues/243
  connect p4.subTypeOut -> p5.superTypeIn;
    // ERROR String -> String
  connect p6.tOut -> outString2;
  // correct Integer -> Integer
  connect inInteger2 -> p9.tIn;  
  
  
  connect p1.pOut -> outString;
      // Correct
  
  connect inInteger -> p1.pIn;
      // ERROR: The types 'java.lang.Integer' and 'java.lang.String' 
      // from the ports in the connector 'inInteger -> p1.portIn'
      // are not compatible!
  
  connect inInteger2 -> p6.tIn; 
      // ERROR: The types 'java.lang.Integer' and 'java.lang.String' 
      // from the ports in the connector 'inInteger2 -> p6.tIn' 
      // are not compatible!
  
  connect p3.bool -> p2.portIn;
      // ERROR: The types 'java.lang.Boolean' and 'java.lang.String' 
      // from the ports in the connector 'p3.Boolean -> p2.inInteger' 
      // are not compatible!
  
  connect p4.superTypeOut -> p5.subTypeIn; 
      // ERROR: The types 'TypesWithHierarchy.SuperType' and 'TypesWithHierarchy.SubType'
      // from the ports in the connector 'p4.superTypeOut -> p5.subTypeIn' 
      // are not compatible!
  
  connect p7.tOut -> p8.tIn;
      // ERROR: The types 'java.lang.String' and 'java.lang.Integer' 
      // from the ports in the connector 'p7.tOut -> p8.tIn' 
      // are not compatible!
}
