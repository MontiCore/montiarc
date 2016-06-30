package a;
import c.*;
import b.NotExistentToo;

component ExistingReferencedComponents {
    port 
        in String s1,
        out String sout1,
        out String sout2,
        out String sout3;
        
    component CorrectCompInA ccia;
    
    component b.CorrectCompInB ccib;
    
    component CorrectCompInC ccic;
    
    component NotExistent ne;
    
    component NotExistentToo;
    
    connect s1 -> ccia.stringIn, ccib.stringIn, ccic.stringIn;
    connect ccia.stringOut -> sout1;
    connect ccib.stringOut -> sout2;
    connect ccic.stringOut -> sout3;
    
}