/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

import java.lang.System;
import java.io.PrintStream;

/**
 * Valid model.
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
