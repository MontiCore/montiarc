package components.head.parameters;

/**
 * Invalid model. ComponentWithDefaultParameters expects String, int,
 * object.
 */ 
component ComposedTestComponent {

  component ComponentWithDefaultParameters("1st") first;
  component ComponentWithDefaultParameters("2nd", 42) second;
  
  // Error: Last argument should be object.
  component ComponentWithDefaultParameters("3rd", 3, new Integer(7)) third;
}