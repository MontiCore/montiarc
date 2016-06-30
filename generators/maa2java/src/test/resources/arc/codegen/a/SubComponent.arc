package a;

component SubComponent {
    
    port 
        in String sIn,
        out String sOut;
        
    connect sIn -> c.sIn;
    connect c.sOut -> sOut;
}