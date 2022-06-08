/* (c) https://github.com/MontiCore/monticore */
package nestedComponents;
/*
 * Valid model.
 */
component WithInnerComponents {
  component Inner1 {}
  Inner1 inr1;

  component Inner2 inr2, inr22 {}

  component Nested nest1 {
    component NextNested1 {}
    NextNested1 nn1;

    component NextNested2 nn2 {}
  }
}