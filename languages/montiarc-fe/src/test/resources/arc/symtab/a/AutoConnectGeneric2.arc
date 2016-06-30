package a;

import a.myTypes.*;

component AutoConnectGeneric2<T extends DBInterface & DBInterface2<Boolean, Integer>> {

    port
        in T myStrIn,
        out T myStrOut;
}