/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: The assigned value of the subcomponent parameter does not match the parameter's type.
 */
component TypeMismatchOfConfigurationParameterValue1 {
  component A(boolean b) { }
  A a(5);
}
