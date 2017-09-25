package invalid;

component NestedComponentWithTypeParameterLacksInstance {
  component A {
    component Nested<T> {
      port in T tIn;
    }
  }
}