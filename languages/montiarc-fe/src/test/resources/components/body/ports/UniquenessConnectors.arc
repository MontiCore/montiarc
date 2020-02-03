/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

import components.body.subcomponents._subcomponents.StringAndIntegerInputAndOutput;
import components.body.subcomponents._subcomponents.package1.ValidComponentInPackage1;
import components.body.subcomponents._subcomponents.package2.ValidComponentInPackage2;

/*
 * Invalid model. Ports have multiple incoming connectors
 * Produces 4 errors in MontiArc3.
 *
 * @implements [Hab16] R1: Each outgoing port of a component type definition
 *                           is used at most once as target of a connector.
 *                           (p. 63, Lst. 3.36)
 * @implements [Hab16] R2: Each incoming port of a subcomponent is used at
 *                           most once as target of a connector.
 *                           (p. 62, Lst. 3.37)
 * TODO: Enable Test
 * TODO: Autoconnect is not working or the CoCO that is checking whether ports used in a connector exist
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
    
    component ValidComponentInPackage1 correctA [stringOut -> s2];
      // ERROR: Only one incoming transition is allowed from a subcomponent
      // to the outgoing port s2.
    
    component StringAndIntegerInputAndOutput tito;
    
    
    connect s1 -> correctA.stringIn, 
                  correctB.stringIn,
                  tito.stringIn; // Connects to tito.stringIn
    
    connect s4 -> correctA.stringIn; // ERROR: Only one incoming transition is
                                     // allowed for an input port.
    
    connect intIn -> tito.intIn; // Connects intInt to tito.intIn
    
    connect tito.intOut -> intOut, // Connects tito.intOut to intOut
                    strOut; // Connects tito.stringOut to strOut
}