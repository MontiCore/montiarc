/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.delegated;

/**
 * tests whether behavior is delegated to a class if not otherwise specified
 */
component KotlinBehaviorDelegate {

  port in int inputValue;
  port out int outputValue;
}