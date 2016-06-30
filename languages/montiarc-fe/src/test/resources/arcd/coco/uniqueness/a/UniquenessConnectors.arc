package a;
import b.*;

component UniquenessConnectors {
    
    port 
        in String s1,
        out String s2,
        out String s3,
        in String s4;
    
    component CorrectCompInB correctB [stringOut -> s2, s3];
    
    component CorrectCompInA correctA [stringOut -> s2];
    
    connect s1 -> correctA.stringIn, correctB.stringIn;
    
    connect s4 -> correctA.stringIn;
    
}