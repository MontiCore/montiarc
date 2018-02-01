package components.body.ports;

/**
 * Invalid mode. Ports should start with a lower case letter.
 */
component PortWithUpperCaseName {
    
    port
        in String Violation, // Error: Port name starts with a capital letter
        out Boolean nonViolation;
}