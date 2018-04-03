package components.body.autoconnect;

import components.body.autoconnect.datatypes.*;

/**
 * Valid model.
 */
component AutoConnectGenericUsage {
    
    autoconnect type;
    
    port 
        in DBType strIn,
        out DBType strOut;
    
    component AutoConnectGeneric<DBType> myGeneric;
    

}