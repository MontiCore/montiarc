/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

import types.*;
import components.body.subcomponents._subcomponents.package1
          .ValidComponentInPackage1;

/*
 * Invalid model.
 *
 * @implements [Hab16] R8: The target port in a connection has to be compatible
 *  to the source port, i.e., the type of the target port is identical or a
 *  supertype of the source port type. (p. 66, lst. 3.43)
 */
component PortCompatibilityTypeInheritance {

  port
    in Integer intIn,
    out Integer intOut1,
    out Integer intOut2;

  component ValidComponentInPackage1 ccia [stringOut -> intOut1];
    // Error: Cannot connect port stringOut of type 'java.lang.String'
    // to connector 'intOut1' of type 'java.lang.Integer'

  component ValidComponentInPackage1 ccia2;

  connect intIn -> ccia.stringIn, ccia2.stringIn;
  //ERROR: Incompatible types 'Integer', 'String'

  connect ccia2.stringOut -> intOut2;
  //ERROR: Incompatible types 'String', 'Integer'


  port
    in SuperType supIn,
    in SubType subIn,
    out SuperType supOut1,
    out SubType subOut1,
    out SuperType supOut2,
    out SubType subOut2;

  component ComponentWithJavaTypes
      cwjt [supOut -> subOut1, supOut1;  // incompatible (SuperType -> SubType)
             subOut -> subOut2, supOut2]; // ERROR: see #241, #243

  connect supIn -> cwjt.supIn1; // compatible
  connect subIn -> cwjt.supIn2, // ERROR: See #241, #243
                   cwjt.subIn2; // compatible

  connect supIn -> cwjt.subIn1; // incompatible

  port
    in String strIn1,
    in Boolean boolIn1,
    out Integer intOut3,
    out Boolean boolOut1;

  component GenericComponent<String, Integer> myGenComp;

  connect strIn1 -> myGenComp.myKIn1;
  connect myGenComp.myVOut -> intOut3;

  component GenericComponent<String, Integer> myGenComp2;

  connect boolIn1 -> myGenComp2.myKIn1;
  connect myGenComp2.myVOut -> boolOut1;
}