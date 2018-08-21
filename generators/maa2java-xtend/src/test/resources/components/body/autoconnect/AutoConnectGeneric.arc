package components.body.autoconnect;

import components.body.autoconnect.datatypes.*;

component AutoConnectGeneric<T extends DBInterface> {

    port
        in T myStrIn,
        out T myStrOut;
}