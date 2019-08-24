/* (c) https://github.com/MontiCore/monticore */
package components.body.autoconnect;

import components.body.autoconnect.dummycomponents.*;

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
    
    component DummyComponent1 a;
    
    component DummyComponent2 b;
    
    component DummyComponent3 c;
    
    component DummyComponent4 d;
    
    connect strIn -> a.strIn;
    connect c.bb -> d.bool;
    connect d.intOut -> intOut;
    
    /** expected additional connectors:
        intIn -> b.intIn;
        a.data -> d.dataSthElse; 
    */
}
