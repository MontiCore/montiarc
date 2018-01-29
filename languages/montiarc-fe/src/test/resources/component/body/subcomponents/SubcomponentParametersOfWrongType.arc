package component.body.subcomponents;

component SubcomponentParametersOfWrongType {
 
  component Buffer(5) subcomp1;
  component Buffer("foo") subcomp2;
}