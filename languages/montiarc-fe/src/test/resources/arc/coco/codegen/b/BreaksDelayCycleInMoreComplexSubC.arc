package b;

/*
 * Valid model.
 * Used to check correctness of the CoCo for undelayed message cycles
 *
 * @author Arne Haber
 */
component BreaksDelayCycleInMoreComplexSubC {

    port
        in String sIn,
        out String sOut;

    component CompWithStringInputAndOutput a1, a2;
    component CompWithTwoStringInputsAndOutput c;
    
    component UsingTwoComponents usingTwo;
    
    connect sIn -> c.sIn1;
    connect c.sOut -> a1.sIn;
    connect a1.sOut -> sOut, usingTwo.sIn1;
    connect a2.sOut -> usingTwo.sIn2, sOut;

    connect usingTwo.sOut -> c.sIn2;
}
