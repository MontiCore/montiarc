/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

/**
 * Invalid model.
 * Nested needs to specify an instance name or an explicit instance.
 *
 * @implements No literature reference
 */
component NestedComponentWithTypeParameterLacksInstance {
  component A {
    component Nested<T> {
      port in T tIn;
    }
  }
}
