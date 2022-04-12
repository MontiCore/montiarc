/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.delegated;

//import java.lang.String;

/**
 * tests whether behavior is delegated to a class if not otherwise specified
 */
component ParameterizedBehaviorDelegate(int parameterValue) {

  port in int inputValue;
  port out int outputValue;
}