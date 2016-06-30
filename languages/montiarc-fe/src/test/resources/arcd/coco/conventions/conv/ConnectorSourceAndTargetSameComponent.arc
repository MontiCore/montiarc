package conv;

component ConnectorSourceAndTargetSameComponent {
    port
        in String s1,
        out String s2;
        
    // not allowed
    connect s1 -> s2;
    
    component Inner1 {
        port 
            in String s3,
            out String s4;
        
        // not allowed
        connect s3 -> s4;
    }

}