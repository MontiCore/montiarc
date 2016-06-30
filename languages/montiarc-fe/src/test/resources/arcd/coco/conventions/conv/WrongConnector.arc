package conv;

component WrongConnector {
    port 
        in String sIn,
        out String sOut; 
    
    component Inner {
        port 
            in String sInInner,
            out String sOutInner;
    }
    
    component Inner 
        myInner [myInner.sOutInner -> sOut];
    
    component Inner
        myInner2 [sOutInner -> myInner.sth.sInInner],
        myInner3,
        myInner4;

    connect myInner3.bla.sOutInner -> myInner2.sInInner;
    
    connect myInner4.sOutInner -> myInner3.bla.sInInner;
    
    
    
}