package a;

import a.myTypes.*;

component AutoConnectGeneric<T extends DBInterface> {

    port
        in T myStrIn,
        out T myStrOut;
}