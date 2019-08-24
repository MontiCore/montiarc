/* (c) https://github.com/MontiCore/monticore */
package components.body.autoconnect;

import types.database.DBType;

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
