package conv;

component PortViolatesNamingConventions {
    
    port
        in String Violation, // Port name starts with a capital letter
        out Boolean nonViolation;
}