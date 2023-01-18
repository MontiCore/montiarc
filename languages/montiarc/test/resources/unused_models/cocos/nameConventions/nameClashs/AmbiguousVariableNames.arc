/* (c) https://github.com/MontiCore/monticore */
package components.body.variables;

/*
 * Invalid model. Variable names 'a' and 'string' used twice.
 *
 * @implements [Wor16] AU3: The names of all inputs, outputs, and variables
 *  are unique. (p. 98. Lst. 5.10)
 * @implements [Hab16] B1: All names of model elements within a component
 *  namespace have to be unique. (p. 59. Lst. 3.31)
 * @implements [Wor16] MU1: The name of each component variable is unique
 *  among ports, variables, and configuration parameters. (p.54, Lst. 4.5)
 */
component AmbiguousVariableNames {
  Integer a;
  Double a;
}
