package components.body.autoconnect;

import components.body.autoconnect.datatypes.*;

component AutoConnectGenericPorts {
    
    autoconnect type;
    
    port 
        in GenericType<String> strIn,
        out GenericType<String> strOut;
    
    component AutoConnectGeneric<GenericType<Integer>> myGenericInt;
    component AutoConnectGeneric<GenericType<String>> myGenericStr;

    
    /* expected additional connectors
    strIn -> myGenericStr.myStrIn;
    myGenericStr.myStrOut -> strOut;
    */
    
    /* forbidden additional connectors
    strIn -> myGenericInt.myStrIn;
    myGenericInt.myStrOut -> strOut;
    */

}