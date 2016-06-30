package a;
import b.CorrectCompInB;

component UniquenessReferences {
    port 
        in String s1,
        out String sOut1,
        out String sOut2,
        out String sOut3,
        out String sOut4,
        out String sOut5;

    component CorrectCompInB;
    
    component CorrectCompInB anotherInB;
    
    component CorrectCompInB correctCompInB;
    
    component CorrectCompInA;
    
    component CorrectCompInA anotherInA;
    
    connect s1 -> anotherInB.stringIn, correctCompInB.stringIn, anotherInA.stringIn, correctCompInA.stringIn;
    connect correctCompInB.stringOut -> sOut1;
    connect anotherInB.stringOut -> sOut2;
    connect correctCompInB.stringOut -> sOut3;
    connect correctCompInA.stringOut -> sOut4;
    connect anotherInA.stringOut -> sOut5;

}