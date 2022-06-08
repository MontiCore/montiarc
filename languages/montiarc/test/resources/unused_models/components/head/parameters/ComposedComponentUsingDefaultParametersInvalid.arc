/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

/**
 * Invalid model.
 *
 * @implements [Wor16] MR1: Arguments of configuration parameters with default
 * values may be omitted during subcomponent declaration. (p. 58, Lst. 4.11)
 */
component ComposedComponentUsingDefaultParametersInvalid {
  component ComponentWithDefaultParameters c0; //Too few arguments
  component ComponentWithDefaultParameters("Welt", 6, new Object(), 1.0) c1; //Too many arguments
}
