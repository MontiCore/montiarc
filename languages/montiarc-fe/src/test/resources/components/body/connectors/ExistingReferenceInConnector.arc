package components.body.connectors;

import components.body.subcomponents._subcomponents.HasStringInputAndOutput;

/**
* Invalid model. See comments below.
* @implements [Hab16] R5: The first part of a qualified connector’s source respectively
* target must correspond to a subcomponent declared in the current component definition. (p.64 Lst. 3.40)
* @implements [Hab16] R6: The second part of a qualified connector’s source respectively 
* target must correspond to a port name of the referenced subcomponent determined by the first part. (p.64 Lst. 3.41)
*/
component ExistingReferenceInConnector {
    port
        in String strIn,
        out String strOut;
        
    component HasStringInputAndOutput cc [pOut -> wrongTarget.strIn]; //R6
    
    connect strIn -> ccWrong.stringIn; //R6
    connect ccWrong.stringOut -> strOut; //R5
    connect strIn -> cc.pIn;
}