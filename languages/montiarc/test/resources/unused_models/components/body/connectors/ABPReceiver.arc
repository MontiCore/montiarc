/* (c) https://github.com/MontiCore/monticore */
package components.body.connectors;

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
