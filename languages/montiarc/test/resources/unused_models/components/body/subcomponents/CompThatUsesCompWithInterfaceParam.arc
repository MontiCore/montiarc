/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import types.*;
import components.head.parameters.CompWithInterfaceParam;

/*
 * Valid model.
 */
component CompThatUsesCompWithInterfaceParam {

    component CompWithInterfaceParam<String>(new ImplementsGenericInterface());
}
