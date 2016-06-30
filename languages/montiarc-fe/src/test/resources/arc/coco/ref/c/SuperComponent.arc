package c;

component SuperComponent {
    port 
        in String sInSc,
        out String sOutSc;
    
    component CorrectCompInC c1, c2;
    
    connect sInSc -> c1.stringIn;
    
    connect sInSc -> c2.stringIn;
    
    connect c1.stringOut -> sOutSc; 

}