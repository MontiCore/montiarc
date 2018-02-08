package components.body.subcomponents;

/**
 * Invalid model. Buffer expects a String parameter.
 *
 * TODO: Pruefen unter welche CoCo dieser Test genau faellt. Moeglich:
 * @implements [Wor16] MT7: Default values of parameters conform to their type. (p. 64 Lst. 4.22)
 */ 
component SubcomponentParametersOfWrongType {
 
  component Buffer(5) subcomp1; // Wrong argument
  component Buffer("foo") subcomp2;
}