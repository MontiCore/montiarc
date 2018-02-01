package components.body.subcomponents;

/**
 * Invalid model. Nested needs to specify an instance name.
 */
component NestedComponentWithTypeParameterLacksInstance {
  component A {
    component Nested<T> {
      port in T tIn;
    }
  }
}