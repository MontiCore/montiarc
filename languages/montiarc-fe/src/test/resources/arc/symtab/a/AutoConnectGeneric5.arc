package a;

import a.myTypes.*;

component AutoConnectGeneric5<T extends DBInterface2<? super Integer, ? extends Boolean>> {

    port
        in T myStrIn,
        out T myStrOut;
}