/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Invalid model: The default value of the parameter 'a' references the
 * parameter 'b', which is declared after it.
 */
component ParameterDefaultForwardReference(int a = b + 1, int b = a) { }
