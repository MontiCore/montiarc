package a;
import ma.sim.*;

component ComponentWithAllowedSelfLoop {
    
    timing causalsync;
    
    port 
        in String sIn,
        out Integer intOut;
    
    component Inner i {
        timing causalsync;
        port 
            in String sIn,
            in Integer intIn,
            out Integer intOut;    
    }
    
    connect sIn -> i.sIn;
    connect i.intOut -> intOut, i.intIn; 
}