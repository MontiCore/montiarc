package pretty;

component Ports {
    port in Long;
    
    port out Integer;
    
    port 
        in String s1,
        out String s2;
        
    port
        in String s3,
        out String s6; 
    
    port
        in Double,
        out Float;
}