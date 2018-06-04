package components.body.autoconnect;

import components.body.autoconnect.datatypes.*;

/*
 * Valid model.
 */
component AutoConnectGeneric<T extends DBInterface> {

    port
        in T myStrIn,
        out T myStrOut;
}