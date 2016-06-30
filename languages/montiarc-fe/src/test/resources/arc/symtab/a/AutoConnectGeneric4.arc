package a;

import a.myTypes.*;

component AutoConnectGeneric4<T extends DBInterface2<? super Integer, ? extends NewType<Boolean[], Integer[][][]>>> {

    port
        in T myStrIn,
        out T myStrOut;
}