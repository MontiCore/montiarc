package a;

component ComponentWithSubComponent {
    
    port 
        in String sIn,
        out String sOut;
        
    component SubComponent c;
    
    connect sIn -> c.sIn;
    connect c.sOut -> sOut;
}