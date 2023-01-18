/* (c) https://github.com/MontiCore/monticore */
package noSubComponentReferenceCycles;

/**
 * Valid model.
 */
component WithoutSubCompRefCycle {
  DummyWithoutCycle du1, du2;
  component Foo {
    DummyWithoutCycle innerDummy;
  }
  Foo foo1;
}
