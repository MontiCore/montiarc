package b;

/*
 * Invalid model. Contains one communication cycle.
 *
 * @implements [Hab16] CG1: Communication cycles without delay should be avoided. (p. 73 Lst. 3.55)
 */
component DoesNotBreakDelayCycleInMoreComplexSubC {

    port
        in String sIn,
        out String sOut;

    component CompWithStringInputAndOutput a1, a2;
    component CompWithTwoStringInputsAndOutput c;
    
    component UsingMultipleComponents usingMultiple;
    
    connect sIn -> c.sIn1;
    connect c.sOut -> a1.sIn;
    connect a1.sOut -> a2.sIn, usingMultiple.sIn1;
    connect a2.sOut -> usingMultiple.sIn2, sOut;
    
    connect usingMultiple.sOut -> c.sIn2;
    
}
