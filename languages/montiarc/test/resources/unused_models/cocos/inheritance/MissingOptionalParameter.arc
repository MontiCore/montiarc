/* (c) https://github.com/MontiCore/monticore */
package components.head.inheritance;

/*
 * Invalid model.
 * Does not explicitly state the third optional parameter.
 *
 * @implements [Hab16] R14: Components that inherit from a parametrized
 *    component provide configuration parameters with the same types,
 *    but are allowed to provide more parameters. (p.69 Lst. 3.49)
 */
component MissingOptionalParameter(Integer x, String y) extends HasRequiredAndOptionalConfigParameters {
}
