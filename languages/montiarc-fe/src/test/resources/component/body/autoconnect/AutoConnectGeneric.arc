package component.body.autoconnect;

import component.body.autoconnect.datatypes.*;

component AutoConnectGeneric<T extends DBInterface> {

    port
        in T myStrIn,
        out T myStrOut;
}