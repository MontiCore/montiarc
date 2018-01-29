package invalid;


component ComplexInvalidParametersOrder(
    int a, 
    int x = 5, 
    String y = "i am a your father", 
    Integer i = new Integer(1+2),
    int thisShouldBeADefaultParameterButItIsNot) {

}