package components.head.parameters;

/**
 * Invalid model. The parameter normalParameter is no default parameter
 * but follows after two default parameters. 
 */
component DefaultParametersInIncorrectOrder (
    int a, 
    int x = 5, 
    String y = "i am a your father", 
    Integer i = new Integer(1+2),
    int normalParameter) 
{
}