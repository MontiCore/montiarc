package components.head.parameters;

/**
 * Invalid model. There are two parameters of name 'a'.
 *
 * @implements [Wor16] MU1: The name of each component variable is unique among ports, variables, and configuration parameters. (p. 54 Lst. 4.5)
 * @implements [Hab16] B1: All names of model elements within a component
 *    namespace have to be unique. (p. 59. Lst. 3.31)
 */
component ParameterAmbiguous(String a, int b, Object a) 
{
}