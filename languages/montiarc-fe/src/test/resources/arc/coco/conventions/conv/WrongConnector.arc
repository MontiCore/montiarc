package conv;

component WrongConnector {
    port 
        in String sIn,
        out String sOut,
        out String sOut2; 
    
    component Inner {
        port 
            in String sInInner,
            out String sOutInner;
    }
    
    component Inner 
        myInner [myInner.sOutInner -> sOut]; // Source is qualified in the connector definition
    
    component Inner
        myInner2 [sOutInner -> myInner.sth.sInInner], //myInner has no subcomponent sth with port sInInner
        myInner3,
        myInner4;

    connect myInner3.bla.sOutInner -> myInner2.sInInner;  // myInner3 has no subcomponent with name "bla" and port "sOutInner"
    
    connect myInner4.sOutInner -> myInner3.bla.sInInner;  //myInner3 has no subcomponent with name "bla" and port "sInInner"
    
    connect sIn -> myInner4.sInInner;
    
    connect sIn -> myInner.sInInner;
    connect sIn -> myInner3.sInInner;
    connect myInner3.sOutInner -> sOut2;
    
}