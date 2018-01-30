package component.body.autoconnect;

import component.body.autoconnect.dummycomponents.*;

/**
 * Invalid model. Various duplicate autoconnection matches and unused 
 * ports.
 */
component AutoConnectType {
    autoconnect type;
    
    port 
        in String strIn,
        in Integer intIn,
        out String strOut,
        out String strOut2,
        out Integer intOut;
    
    component A;
    
    component B;
    
    component C;
    
    component D;
    
    connect strIn -> a.strIn;
    connect c.bb -> d.bool;
    connect d.intOut -> intOut;
    
    /** expected additional connectors:
        intIn -> b.intIn;
        a.data -> d.dataSthElse; 
    */
}