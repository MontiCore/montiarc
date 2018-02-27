package a;

import components._subcomponents.package1.ValidComponentInPackage1;

component UniqueNamesDifferentSymboltypes<myName>(int myName) {
    
    port
        in String myName,
        out String sOut;
    
    component ValidComponentInPackage1 myName;
    
    connect myName -> myName.stringIn;
    connect myName.stringOut -> sOut;
    
    java inv myName: {
        assert x==1;
    };

}