package components.body.connectors;

import components._subcomponents.TwoInTwoOut;
import components._subcomponents.package1.ValidComponentInPackage1;

/*
 * Invalid model. Ports have multiple incoming connectors
 * Produces 4 errors in MontiArc3.
 *
 * @implements TODO
 * TODO: Add Test
 */
component UniquenessConnectors {
    
    port 
        in String s1,
        out String s2,
        out String s3,
        in String s4,
        out Integer intOut,
        out String strOut,
        in Integer intIn;
    
    component ValidComponentInPackage2 correctB [stringOut -> s2, s3];
      // ERROR: Only one incoming transition is allowed from a subcomponent
      // to the outgoing port s2.
    
    component ValidComponentInPackage1 correctA [stringOut -> s2];
      // ERROR: Only one incoming transition is allowed from a subcomponent
      // to the outgoing port s2.
    
    component TwoInTwoOut tito;
    
    
    connect s1 -> correctA.stringIn, // ERROR: Only one incoming transition is
                                     // allowed for an input port.
                  correctB.stringIn,
                  tito; // Connects to tito.stringIn
    
    connect s4 -> correctA.stringIn; // ERROR: Only one incoming transition is
                                     // allowed for an input port.
    
    connect intIn -> tito; // Connects intInt to tito.intIn
    
    connect tito -> intOut, // Connects tito.intOut to intOut
                    strOut; // Connects tito.stringOut to strOut
}