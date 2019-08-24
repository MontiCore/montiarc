/* (c) https://github.com/MontiCore/monticore */
package components.body.autoconnect;

import components.body.autoconnect.datatypes.*;
import types.GenericType;

/*
 * Invalid model.
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
