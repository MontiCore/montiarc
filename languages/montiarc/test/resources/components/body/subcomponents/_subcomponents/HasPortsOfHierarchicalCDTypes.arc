/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents._subcomponents;

import types.TypesWithHierarchy.SubType;
import types.TypesWithHierarchy.SuperType;

/*
 * Valid component.
 * Has ports with custom datatypes defined in a CD, where SubType extends SuperType.
 *
 * Formerly named "R6PartnerOne" in MontiArc3.
 */
component HasPortsOfHierarchicalCDTypes {

  port 
    in SubType subTypeIn,
    out SubType subTypeOut,
    in SuperType superTypeIn,
    out SuperType superTypeOut;
}
