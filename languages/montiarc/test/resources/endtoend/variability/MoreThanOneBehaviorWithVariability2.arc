/* (c) https://github.com/MontiCore/monticore */
package variability;

/**
 * Invalid model: The component has two compute blocks in configuration f1 && f2
 * (more than one behavior description).
 */
component MoreThanOneBehaviorWithVariability2 {

  feature f1, f2;

  varif(f1) { compute { } }
  varif(f2) { compute { } }

}
