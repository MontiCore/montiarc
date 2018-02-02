package components.head.inheritance;

/**
 * Valid model. 
 */
component ComposedComponentUsingInheritedPorts {
  port in Integer inputIntegerA;
  port in String inputStringA;

  component ExtendsSuperComponent esc;

  connect inputIntegerA -> esc.inputInteger;
  connect inputStringA -> esc.inputString;
}
