package a;
import b.CorrectCompInB;

component UniquenessReferences2 {
    port 
        in String s1,
        out String sOut;
        
    component CorrectCompInB;
    
    component CorrectCompInB;
    
    component CorrectCompInB ccib;
    
    component CorrectCompInA anotherInA;
    
    component CorrectCompInA anotherInA;
    
    connect s1 -> ccib.stringIn;
    connect ccib.stringOut -> sOut;
}