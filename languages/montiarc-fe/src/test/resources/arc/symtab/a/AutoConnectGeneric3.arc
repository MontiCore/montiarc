package a;

import a.myTypes.*;

component AutoConnectGeneric3<T extends DBInterface2<Boolean[], Integer[][]>> {

    port
        in T myStrIn,
        out T myStrOut;
}