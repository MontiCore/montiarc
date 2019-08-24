/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

import components.body.subcomponents._subcomponents.ConfigurableBasicComponent;
import types.*;
/**
 * Invalid model.
 * Multiple subcomponent instances of the same name.
 *
 * @implements [Hab16] B1: All names of model elements within a component 
 * namespace have to be unique. (p. 59. Lst. 3.31)
 */
 
component ConfigurableArchitectureComponent(Person x, int y, String z) {
  
  component ConfigurableBasicComponent(x, 6, MyEnum.FIRST) cbc;
  
  component ConfigurableBasicComponent( 2 * ( 2 + x ), 6, MyEnum.SECOND) cbc;
  
  component ConfigurableBasicComponent( 2 * ( 2 + (2 / x) * 25 ), 6, MyEnum.THIRD) cbc;
}
