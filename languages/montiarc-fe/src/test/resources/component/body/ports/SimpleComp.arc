package component.body.ports;

/**
 * Valid model. Used by UnconnectedPorts.
 */
component SimpleComp {
    port
        in Integer usedInputInteger,
        out Integer usedOutputInteger,
        out Integer unusedSimpleCompOutputInteger;
}