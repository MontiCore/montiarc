/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Invalid model: The inner components indirectly extend themselves, as they
 * extend each other (cyclic inheritance).
 */
component CircularInheritance4 {
  component Inner1 extends Inner2 { }
  component Inner2 extends Inner1 { }
}
