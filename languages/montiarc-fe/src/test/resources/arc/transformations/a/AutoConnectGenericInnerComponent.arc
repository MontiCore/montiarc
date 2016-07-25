package a;

import testtypes.*;

component AutoConnectGenericInnerComponent {
    
    autoconnect type;
    
    port 
        in DBType strIn,
        in Integer intIn,
        out DBType strOut,
        out Integer intOut;
    
    component Inner<T> myGeneric<DBType> {
      port
        in T myStrIn,
        out T myStrOut;
    }
    
    component Inner<Integer> a;
    

}