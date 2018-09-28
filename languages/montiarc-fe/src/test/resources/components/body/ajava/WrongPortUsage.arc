package components.body.ajava;

import java.util.Collections;
/**
 * Invalid model.
 * Bad usage of ports (see below)
 *
 * @implements: There is no AJava literature
 */
component WrongPortUsage {
  
  port
    in String input,
    out String output;
  
  compute PrintInput {
    String buffer = output;   // Cannot read from outgoing ports
    Collections.emptyList();
    bubu = 0;             // Port does not exist
    input = bubu;             // Cannot assign values to incoming ports + used variable does not exist
  }
}
