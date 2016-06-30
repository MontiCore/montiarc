package pretty;

component ConfigurableArchitectureComponent(Person x, int y, String z) {
  
  component ConfigurableBasicComponent(x, 6, EnumType.Test) cbc;
  
  component ConfigurableBasicComponent( 2 * ( 2 + x ), 6, EnumType.Test) cbc;
  
  component ConfigurableBasicComponent( 2 * ( 2 + (2 / x) * 25 ), 6, EnumType.Test) cbc;
}