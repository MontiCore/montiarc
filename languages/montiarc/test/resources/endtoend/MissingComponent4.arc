/* (c) https://github.com/MontiCore/monticore */

/**
 * Invalid model: The type Missing of subcomponent sub of inner component Inner
 * is missing (the component cannot be resolved).
 */
component MissingComponent4 {
  component Inner {
    Missing sub;
  }
}
