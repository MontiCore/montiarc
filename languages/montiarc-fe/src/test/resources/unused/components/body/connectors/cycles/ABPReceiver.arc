package components.body.connectors.cycles;

/**
 * Used by ABP
 */
component ABPReceiver {

    port
        in ABPMessage abpMessage,
        out Boolean ack,
        out String message;
}