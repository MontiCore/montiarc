package components.head.inheritance;

/*
 * Invalid model.
 * Too few configuration parameters and wrong type.
 *
 * @implements [Hab16] R14: Components that inherit from a parametrized
 *    component provide configuration parameters with the same types,
 *    but are allowed to provide more parameters. (p.69 Lst. 3.49)
 */
component ExtendsSubCompAndOptionalConfigParameters(String param)
    extends SubCompAndOptionalConfigParameters {

}