package components.head.inheritance;

/**
 * Valid model. 
 */
component SubB extends SuperArchitecturalComponent {
    
    port 
        in String anotherIn,
        out String anotherOut,
        out String anotherOut2;    
    
    component A anotherA [strOut->anotherOut2];
    component A yetAnotherA ;
    
    connect myA.strOut -> anotherOut;
    connect anotherIn -> anotherA.strIn; 
    
    connect yetAnotherA.strOut -> myOutput519;
}