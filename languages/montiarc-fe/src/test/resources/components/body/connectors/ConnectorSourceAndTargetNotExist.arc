package components.body.connectors;

import components.body.subcomponents._subcomponents.package1.ValidComponentInPackage1;
import components.body.subcomponents._subcomponents.package2.ValidComponentInPackage2;

/*
 * Invalid model.
 *
 * @implements [Hab16] R5: The first part of a qualified connector’s source
 *  respectively target must correspond to a subcomponent declared in the
 *  current component definition. (p.64 Lst. 3.40)
 * @implements [Hab16] R6: The second part of a qualified connector’s source
 *  respectively target must correspond to a port name of the referenced
 *  subcomponent determined by the first part. (p.64 Lst. 3.41)
 * @implements [Hab16] R7: The source port of a simple connector must exist
 *  in the subcomponents type. (p.65 Lst. 3.42)
 */
component ConnectorSourceAndTargetNotExist {
    port
        in String strIn,
        out String strOut1,
        out String strOut2;
        
    component ValidComponentInPackage1 ccia [stringOutWrong -> strOut1];
      // ERROR: Port ccia.stringOutWrong does not exist
    
    component ValidComponentInPackage2 ccib;
    
    connect strIn -> ccib.stringInWrong, ccia.stringIn;
    // ERROR: Connector target does not exist
    
    connect ccib.stringOutWrong -> strOut2;
    // ERROR: Connector source does not exist
}