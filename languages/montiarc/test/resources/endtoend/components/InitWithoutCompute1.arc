/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: The component declares an init block but has no compute block.
 * Additionally, it declares an automaton.
 */
component InitWithoutCompute1 {

  init { int x = 0; }
  automaton { initial state Init; }
}
