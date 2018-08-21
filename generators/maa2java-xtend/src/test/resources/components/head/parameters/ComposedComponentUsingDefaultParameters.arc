package components.head.parameters;

/**
 * Valid model.
 */
component ComposedComponentUsingDefaultParameters {
  component ComponentWithDefaultParameters("Hallo", 10, new Object()) c0;
  component ComponentWithDefaultParameters("Welt") c1;
}