/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

import components.body.subcomponents._subcomponents.package1.ValidComponentInPackage1;
import components.body.subcomponents._subcomponents.package2.ValidComponentInPackage2;

/*
 * Invalid model.
 * See comments below.
 *
 * @implements [Hab16] R5: The first part of a qualified connector’s source
 *  respectively target must correspond to a subcomponent declared in the current
 *  component definition. (p.64 Lst. 3.40)
 * @implements [Hab16] R6: The second part of a qualified connector’s source
 *  respectively target must correspond to a port name of the referenced
 *  subcomponent determined by the first part. (p.64, Lst. 3.41)
 * @implements [Hab16] R7: The source port of a simple connector must exist in
 *  the subcomponents type. (p.65 Lst. 3.42)
 * @implements [Hab16] R8: The target port in a connection has to be compatible
 *  to the source port, i.e., the type of the target port is identical or a
 *  supertype of the source port type. (p. 66, lst. 3.43)
 */
component ExistingPortInConnector {
    port 
        in String strIn,
        out String strOut1,
        out String strOut2;
        
    component ValidComponentInPackage1 ccia [stringOutWrong -> strOut1];
    
    component ValidComponentInPackage2 ccib;
    
    connect strIn -> ccib.stringInWrong, ccia.stringIn;
    
    connect ccib.stringOutWrong -> strOut2;
}
