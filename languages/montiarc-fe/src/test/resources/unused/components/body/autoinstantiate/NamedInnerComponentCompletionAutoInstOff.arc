package unused.components.body.autoinstantiate;

/**
 * Valid model.
 */
component NamedInnerComponentCompletionAutoInstOff {

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
    
    connect sIn -> na.sIn;
    connect na.sOut1 -> sOut1;
}