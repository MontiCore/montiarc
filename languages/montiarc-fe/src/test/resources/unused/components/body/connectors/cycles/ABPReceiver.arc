package components.body.connectors.cycles;

import types.CType;

/**
 * Valid model.
 */
component ABPReceiver {

    port
        in CType abpMessage,
        out Boolean ack,
        out String message;
}