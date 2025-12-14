/* (c) https://github.com/MontiCore/monticore */
package variability;

/**
 * Invalid model: The component has an automaton and a compute block in
 * configuration f1 && f2 (more than one behavior description).
 */
component MoreThanOneBehaviorWithVariability3 {

  feature f1, f2;

  varif(f1) { automaton { initial state S; } }
  varif(f2) { compute { } }

}
