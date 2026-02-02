/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: The component declares an init block but has no compute block.
 */
component InitWithoutCompute3 {
  init { int y = 2; }
}
