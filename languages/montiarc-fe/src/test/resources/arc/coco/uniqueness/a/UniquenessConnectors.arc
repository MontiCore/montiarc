package a;
import b.*;

component UniquenessConnectors {
    
    port 
        in String s1,
        out String s2,
        out String s3,
        in String s4,
        out Integer intOut,
        out String strOut,
        in Integer intIn;
    
    component CorrectCompInB correctB [stringOut -> s2, s3];
    
    component CorrectCompInA correctA [stringOut -> s2];
    
    component TwoInTwoOut tito;
    
    
    connect s1 -> correctA.stringIn, correctB.stringIn, tito;
    
    connect s4 -> correctA.stringIn;
    
    connect intIn -> tito;
    
    connect tito -> intOut, strOut;
}