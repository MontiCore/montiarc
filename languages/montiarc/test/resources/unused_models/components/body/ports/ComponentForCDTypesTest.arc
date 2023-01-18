/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

import types.CDTestTypes.*;

/*
 * Valid model.
 */
component ComponentForCDTypesTest {
  port
    in TypeWithFields portInUnqualified,
    in types.CDTestTypes.TypeWithFields portInQualified,
    in SubTypeOne,
    in SubTypeTwo,
    in InterfaceTypeThree,
    in EnumType;
}
