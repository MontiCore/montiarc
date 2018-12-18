package components.body.autoconnect;

import types.database.*;

/*
 * Valid model.
 */
component AutoConnectGeneric<T extends DBInterface> {

    port
        in T inT,
        out T outT;
}