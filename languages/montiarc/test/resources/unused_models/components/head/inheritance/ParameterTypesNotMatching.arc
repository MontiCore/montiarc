/* (c) https://github.com/MontiCore/monticore */
package components.head.inheritance;

/*
 * Invalid model.
 * Parameter 'param1' is not of type 'java.lang.Integer'.
 *
 * @implements [Hab16] R14: Components that inherit from a parametrized
 *    component provide configuration parameters with the same types,
 *    but are allowed to provide more parameters. (p.69 Lst. 3.49)
 */
component ParameterTypesNotMatching(Double param1, String param2, Object param3) extends HasRequiredAndOptionalConfigParameters{
  // Empty body
}
