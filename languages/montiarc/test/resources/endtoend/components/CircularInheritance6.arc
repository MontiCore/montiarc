/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Invalid model: The inner components of the inner component indirectly extend
 * themselves, as they extend each other (cyclic inheritance).
 */
component CircularInheritance6 {
  component Inner {
    component InnerInner1 extends InnerInner2 { }
    component InnerInner2 extends InnerInner1 { }
  }
}
