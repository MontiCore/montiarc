package unused.components.body.subcomponents;

/**
 * 
 * Invalid model. Multiple subcomponent instances of the same name.
 *
 * @implements [Hab16] B1: All names of model elements within a component 
 * namespace have to be unique. (p. 59. Lst. 3.31)
 *
 */
 
component ConfigurableArchitectureComponent(Person x, int y, String z) {
  
  component ConfigurableBasicComponent(x, 6, EnumType.Test) cbc;
  
  component ConfigurableBasicComponent( 2 * ( 2 + x ), 6, EnumType.Test) cbc;
  
  component ConfigurableBasicComponent( 2 * ( 2 + (2 / x) * 25 ), 6, EnumType.Test) cbc;
}