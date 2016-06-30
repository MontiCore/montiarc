package a;

import testtypes.*;

component AutoConnectGeneric<T extends DBInterface> {

    port
        in T myStrIn,
        out T myStrOut;
}