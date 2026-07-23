/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: The assigned value of the subcomponent parameter does not match the parameter's type.
 */
component TypeMismatchOfConfigurationParameterValue2(int p) {
  component A(boolean b) { }
  A a(p);
}
