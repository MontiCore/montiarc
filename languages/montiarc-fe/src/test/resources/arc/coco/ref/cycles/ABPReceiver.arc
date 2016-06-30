package cycles;

import cycles.ABPMessage;

component ABPReceiver {

    port
        in ABPMessage abpMessage,
        out Boolean ack,
        out String message;
}