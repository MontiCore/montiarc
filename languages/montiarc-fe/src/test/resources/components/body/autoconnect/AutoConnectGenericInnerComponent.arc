package components.body.autoconnect;

import types.database.*;

/**
 * Valid model.
 */
component AutoConnectGenericInnerComponent {
    
    autoconnect type;
    
    port 
        in DBType dbIn,
        in Integer intIn,
        out DBType dbOut,
        out Integer intOut;
    
    component Inner<T> myGeneric<DBType> {
      port
        in T inT,
        out T outT;
    }
    
    component Inner<Integer> a;
    

}