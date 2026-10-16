/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Invalid model: Expressions with side effects are not allowed in default
 * parameter values.
 */
component SideEffectInDefaultParameterValue(int p, int q = p++) { }
