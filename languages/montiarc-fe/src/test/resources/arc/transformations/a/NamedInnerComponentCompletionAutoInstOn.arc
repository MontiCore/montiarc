package a;

component NamedInnerComponentCompletionAutoInstOn {

    port 
        in String sIn,
        out String sOut1;
        
    component NamedInner na {
        port 
            in String sIn,
            out String sOut1;
    }
    
    component OtherNotNamedInnerComponent {
        port 
            in String;
    }
    
    connect sIn -> na.sIn, otherNotNamedInnerComponent;
    connect na.sOut1 -> sOut1;
}