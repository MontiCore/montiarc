/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: The component has two automata (more than one behavior description).
 */
component MoreThanOneBehavior1 {

  automaton { initial state S; }
  automaton { initial state S; }

}
