package a;

component UniqueNamesDifferentSymboltypes<myName>(int myName) {
    
    port
        in String myName,
        out String sOut;
    
    component CorrectCompInA myName;
    
    connect myName -> myName.stringIn;
    connect myName.stringOut -> sOut;
    
    java inv myName: {
        assert x==1;
    };

}