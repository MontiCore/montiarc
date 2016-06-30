package a;

component UniqueInnerCompDefinition {
    
    port 
        in String sIn,
        out String sOut;
        
    component NotUniqueDef {
        port 
            in String sIn,
            out String sOut;
    }
    
    component NotUniqueDef {
        port 
            in String sIn,
            out String sOut;
    }
    
    component NotUniquDefWithInstance {
        port 
            in String sIn,
            out String sOut;
    }
    
    component NotUniquDefWithInstance instanceName {
        port 
            in String sIn,
            out String sOut;
    }
    
    component NotUniqueDefBothInstanceNames n1 {
        port 
            in String sIn,
            out String sOut;
    }
    
    component NotUniqueDefBothInstanceNames n2 {
        port 
            in String sIn,
            out String sOut;
    }
}