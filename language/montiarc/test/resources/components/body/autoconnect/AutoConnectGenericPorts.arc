/* (c) https://github.com/MontiCore/monticore */
package components.body.autoconnect;

import components.body.autoconnect.datatypes.*;
import types.GenericType;

/*
 * Invalid model.
 *
 * @implements [Hab16] R1: Each outgoing port of a component type definition is used at most once
 * as target of a connector. (p. 62, lst. 3.36)
 * @implements [Hab16] R2: Each incoming port of a subcomponent is used at most once
 * as target of a connector. (p.63, lst. 3.37)
 */
component AutoConnectGenericPorts {
    
    autoconnect type;
    
    port 
        in GenericType<String> strIn,
        out GenericType<String> strOut;
    
    component AutoConnectGeneric<GenericType<Integer>> myGenericInt;
    component AutoConnectGeneric<GenericType<String>> myGenericStr;

    
    /* expected additional connectors
    strIn -> myGenericStr.inT;
    myGenericStr.outT -> strOut;
    */
    
    /* forbidden additional connectors
    strIn -> myGenericInt.inT;
    myGenericInt.outT -> strOut;
    */

}