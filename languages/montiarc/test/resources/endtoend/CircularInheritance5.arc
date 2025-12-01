/* (c) https://github.com/MontiCore/monticore */

/*
 * Invalid model: The inner component of the inner component directly extends
 * itself (circular inheritance).
 */
component CircularInheritance5 {
  component Inner {
    component InnerInner extends InnerInner { }
  }
}
