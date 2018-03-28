package components.body.connectors;

import types.CorrectComp;

/**
* Invalid model. See comments below.
*
* @implements [Hab16] R5: The first part of a qualified connector’s source respectively
* target must correspond to a subcomponent declared in the current component definition. (p.64 Lst. 3.40)
* @implements [Hab16] R6: The second part of a qualified connector’s source respectively 
* target must correspond to a port name of the referenced subcomponent determined by the first part. (p.64 Lst. 3.41)
* @implements [Hab16] R7: The source port of a simple connector must exist in the subcomponents
* type. (p.65 Lst. 3.42)
*/
component ConnectorSourceAndTargetNotExist {
    port 
        in String strIn,
        out String strOut1,
        out String strOut2,
        out String strOut3,
        out String strOut4;
        
    component CorrectComp cc1 [stringOutWrong -> strOut1]; // R7 source port does not exist
    
    component CorrectComp cc2;
    
    connect strIn -> cc2.stringInWrong, cc1.stringIn; //R6 first target port does not exist
    
    connect cc2.stringOutWrong -> strOut2; //R5 source port does not exist
    
    // correct connectors
    connect cc1.stringOut -> strOut3;
    connect cc2.stringOut -> strOut4;
    connect strIn -> cc2.stringIn;
}