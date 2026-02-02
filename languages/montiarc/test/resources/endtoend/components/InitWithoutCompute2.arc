/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: The inner component declares an init block but has no compute block.
 * Additionally, the inner component declares an automaton.
 */
component InitWithoutCompute2 {

  component Inner {
    init { int y = 2; }
    automaton { initial state S; }
  }
}
