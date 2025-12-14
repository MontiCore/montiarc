/* (c) https://github.com/MontiCore/monticore */
package variability;

/**
 * Invalid model: The inner component 'Inner' has two automata in
 * configuration f1 && f2 (more than one behavior description).
 */
component MoreThanOneBehaviorWithVariability4 {

  component Inner {
    feature f1, f2;
    varif(f1) { automaton { initial state S; } }
    varif(f2) { automaton { initial state S; } }
  }

}
