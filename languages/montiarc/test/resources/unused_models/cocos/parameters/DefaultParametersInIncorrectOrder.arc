/* (c) https://github.com/MontiCore/monticore */
package components.head.parameters;

/**
 * Invalid model. The parameter normalParameter is no default parameter
 * but follows after three default parameters.
 *
 * @implements [Wor16] MR4: All mandatory component configuration parameters
 *  precede the parameters with default values. (p.60 Lst. 4.14)
 */
component DefaultParametersInIncorrectOrder (
    int a,
    int x = 5,
    String y = "i am a your father",
    Integer i = new Integer(1+2),
    int normalParameter)
{
// Empty body
}
