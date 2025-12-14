/* (c) https://github.com/MontiCore/monticore */
package variability;

/**
 * Invalid model: The component has three automata in configuration f1 && f2 && f3
 * (more than one behavior description).
 */
component MoreThanOneBehaviorWithVariability5 {

  feature f1, f2, f3;
  varif(f1) { automaton { initial state S; } }
  varif(f2) { automaton { initial state S; } }
  varif(f3) { automaton { initial state S; } }

}
