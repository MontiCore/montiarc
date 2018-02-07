package components.body.ports;

/**
 * Invalid model. Ports should start with a lower case letter.
 * @implements [RRW14a] C2: The names of variables and ports start with lowercase letters. (p. 31, Lst. 6.5)  
 */
component PortWithUpperCaseName {
    
    port
        in String Violation, // Error: Port name starts with a capital letter
        out Boolean nonViolation;
}