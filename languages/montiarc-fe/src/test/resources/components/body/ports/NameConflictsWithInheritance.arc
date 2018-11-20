package components.body.ports;

import components.body.subcomponents._subcomponents.ExtendsHasStringInputAndOutput;

/**
 * Invalid model.
 *
 * The names of inherited ports conflict with the name of elements defined
 * in this component
 *
 * @implements [Wor16] AU3: The names of all inputs, outputs, and variables
 * are unique. (p. 98. Lst. 5.10)
 * @implements [Hab16] B1: All names of model elements within a component
 * namespace have to be unique. (p. 59. Lst. 3.31)
 * @implements [Wor16] MU1: The name of each component variable is unique
 *  among ports, variables, and configuration parameters. (p.54, Lst. 4.5)
 */
component NameConflictsWithInheritance extends ExtendsHasStringInputAndOutput{

  port in Integer pIn; // Error: identifier pIn is not unique

  Boolean pOut; // Error: identifier pOut is not unique
}