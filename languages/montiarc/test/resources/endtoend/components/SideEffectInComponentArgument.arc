/* (c) https://github.com/MontiCore/monticore */
package components;

/*
 * Invalid model: Expressions with side effects are not allowed in component
 * arguments.
 */
component SideEffectInComponentArgument(int p) {

  component Inner(int q) { }

  Inner sub(p++);
}
