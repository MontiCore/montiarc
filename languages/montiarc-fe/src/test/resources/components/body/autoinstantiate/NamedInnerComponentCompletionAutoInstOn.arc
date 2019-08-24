/* (c) https://github.com/MontiCore/monticore */
package components.body.autoinstantiate;

/**
 * Valid model.
 */
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
    
    connect sIn -> na.sIn, otherNotNamedInnerComponent.string;
    connect na.sOut1 -> sOut1;
}
