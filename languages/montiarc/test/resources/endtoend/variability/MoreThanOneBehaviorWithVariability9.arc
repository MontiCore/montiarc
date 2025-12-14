/* (c) https://github.com/MontiCore/monticore */
package variability;

/**
 * Invalid model: The component has two automata in configuration f2 && !f1
 * (more than one behavior description).
 */
component MoreThanOneBehaviorWithVariability9 {

  feature f1, f2;

  varif(f1) {
    component Inner { } Inner sub;
  }

  varif(f2) {
    automaton { initial state S; }
    automaton { initial state S; }
  }

  constraint(f1 ^ f2);

}
