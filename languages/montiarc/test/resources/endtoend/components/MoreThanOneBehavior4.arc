/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: The inner component 'Inner' has two automata (more than one
 * behavior description).
 */
component MoreThanOneBehavior4 {

  component Inner {
    automaton { initial state S; }
    automaton { initial state S; }
  }

}
