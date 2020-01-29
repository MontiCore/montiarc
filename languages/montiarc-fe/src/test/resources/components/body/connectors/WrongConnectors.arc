/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

/*
 * Invalid model.
 *
 * @implements [Hab16] CO1: Connectors may not pierce through component interfaces. (p. 60, Lst. 3.33)
 * @implements [Hab16] CO2: A simple connector’s source is an outgoing port of
 * the referenced component type and is therefore not qualified. (p.61, lst. 3.34)
 */
component WrongConnectors {
    port 
        in String sIn,
        out String sOut,
        out String sOut2; 
    
    component Inner {
        port 
            in String sInInner,
            out String sOutInner;
    }
    
    component Inner 
        myInner [myInner.sOutInner -> sOut]; // Source is qualified in the connector definition
    
    component Inner
        myInner2 [sOutInner -> myInner.sth.sInInner], // myInner has no subcomponent sth with port sInInner
        myInner3,
        myInner4;

    connect myInner3.bla.sOutInner -> myInner2.sInInner;  // myInner3 has no subcomponent with name "bla" and port "sOutInner"
    
    connect myInner4.sOutInner -> myInner3.bla.sInInner;  //myInner3 has no subcomponent with name "bla" and port "sInInner"
    
    connect sIn -> myInner4.sInInner;
    
    connect sIn -> myInner.sInInner;
    connect sIn -> myInner3.sInInner;
    connect myInner3.sOutInner -> sOut2;
}