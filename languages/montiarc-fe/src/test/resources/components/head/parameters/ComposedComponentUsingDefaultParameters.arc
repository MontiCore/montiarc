package components.head.parameters;

/**
 * Valid model.
 *
  * @implements [Wor16] MR1: Arguments of configuration parameters with default
 *  values may be omitted during subcomponent declaration. (p. 58, Lst. 4.11)
 */
component ComposedComponentUsingDefaultParameters {
  component ComponentWithDefaultParameters("Hallo", 10, new Object()) c0;
  component ComponentWithDefaultParameters("Welt") c1;
}