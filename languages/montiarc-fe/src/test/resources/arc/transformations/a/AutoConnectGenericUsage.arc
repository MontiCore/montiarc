package a;

import testtypes.*;

component AutoConnectGenericUsage {
    
    autoconnect type;
    
    port 
        in DBType strIn,
        out DBType strOut;
    
    component AutoConnectGeneric<DBType> myGeneric;
    

}