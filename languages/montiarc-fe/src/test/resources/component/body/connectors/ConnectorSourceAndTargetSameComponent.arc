package component.body.connectors;

/**
 * Invalid model. A connector may not connect two ports of the same
 * component.
 */
component ConnectorSourceAndTargetSameComponent {
    port
        in String s1,
        out String s2;
        
    connect s1 -> s2; // Error: Prohibited
    
    component Inner1 {
        port 
            in String s3,
            out String s4;
        
        connect s3 -> s4; // Error: Prohibited
    }
}
