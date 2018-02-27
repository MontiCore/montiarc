package a;

component PortCompatibilitySimple {
    
    port 
        in Integer intIn,
        out Integer intOut1,
        out Integer intOut2;

    component ValidComponentInPackage1 ccia [stringOut -> intOut1]; // incompatible (String -> Integer)
    
    component ValidComponentInPackage1 ccia2;
    
    connect intIn -> ccia.stringIn, ccia2.stringIn; // incompatible (Integer -> String) 2x
    
    connect ccia2.stringOut -> intOut2; // incompatible (String -> Integer)
}
