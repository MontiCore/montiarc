package components.body.ajava;

import java.util.Collections;
/**
 * Invalid model.
 * Bad usage of ports (see below)
 *
 * @implements: There is no AJava literature
 */
component WrongPortUsage(Integer x) {
  
  port
    in String input,
    in Integer i,
    out String output,
    out Integer j;
  
  compute PrintInput {
    String buffer = output;   // Cannot read from outgoing ports
    Collections.emptyList();
    output.charAt(0); //method calls on outgoing ports are not allowed.
    bubu = 0;             // Port does not exist
    input = bubu;             // Cannot assign values to incoming ports + used variable does not exist
    ++i;//error not allowed
    i++;//error not allowed
    x=6;//error not allowed
    ++x;//error not allowed
    x++;//error not allowed
    j++;//error not allowed
    --j;//error not allowed
    
  }
}
