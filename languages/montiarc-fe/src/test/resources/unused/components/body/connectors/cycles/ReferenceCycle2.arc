package components.body.connectors.cycles;

/**
* Invalid model. See comments below.
* @implements R13: Subcomponent reference cycles are forbidden. (p. 68, lst. 3.48)
*/
component ReferenceCycle2 {
    port 
        in Integer portIn;
        
    component ReferenceCycle refCycle;
    
    connect portIn -> refCycle.portIn;
}