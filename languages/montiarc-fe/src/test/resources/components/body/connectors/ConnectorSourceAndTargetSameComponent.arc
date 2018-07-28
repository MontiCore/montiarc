package components.body.connectors;

/**
 * Invalid model.
 * A connector may not connect two ports of the same components.
 */
component ConnectorSourceAndTargetSameComponent {
    port
        in String s1,
        out String s2;

    connect inner1.s4 -> inner1.s4; // Error: Prohibited
    connect s1 -> s2; // Error: Prohibited
    connect s1 -> s1; // Error: Prohibited
    
    component Inner1 {
        port 
            in String s3,
            out String s4;
        
        connect s3 -> s4; // Error: Prohibited
    }

}
