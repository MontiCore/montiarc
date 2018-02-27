package a;

component UniqueNamesDifferentSymboltypes<myName>(int myName) {
    // 4 * myName and 2 * myOtherName => 6 errors
    port
        in String myName,
        out String myOtherName;
        
    component ValidComponentInPackage1 myName;
    component ValidComponentInPackage1 myOtherName;
    
    component InnerComp {
        // myName is not unique in this component, but myOtherName is => 2 errors
        port
            in String myName,
            out String myOtherName;
            
        component ValidComponentInPackage1 myName;
    }
    
    component InnerComp;
}