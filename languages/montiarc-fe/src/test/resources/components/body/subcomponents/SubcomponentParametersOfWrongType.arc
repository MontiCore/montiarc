package components.body.subcomponents;

/**
 * Invalid model. Buffer expects a String parameter.
 */ 
component SubcomponentParametersOfWrongType {
 
  component Buffer(5) subcomp1; // Wrong argument
  component Buffer("foo") subcomp2;
}