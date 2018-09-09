package components.body.ports;

/**
 * Invalid model.
 * TODO: Fix test. Model was intended as a valid model, but is missing imports for some types
 */
component PortsWithStereotypes {

  port  
    in String stringIn, 
    <<disabled="held", initialOutput = "1", ignoreWarning>> in Integer integerIn,
    <<disabled="reset", initialOutput = "0">> out String stringOut,
    <<portStereo1>> out Number,
    <<portStereo2>> out Integer integerOut,
    out System,
    out PrintStream ps;
  
}