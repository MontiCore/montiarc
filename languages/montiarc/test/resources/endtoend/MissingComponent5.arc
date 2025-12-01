/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing of subcomponent sub of inner component Inner2
 * is missing (the component cannot be resolved). While component Missing is
 * defined in the scope of Inner1, it is not accessible in the scope of Inner2.
 */
component MissingComponent5 {
  component Inner {
    component Missing { }
  }
  component Inner2 {
    Missing sub;
  }
}
