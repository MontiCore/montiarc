package components.body.ajava;

/**
 * Invalid model. Bad usage of ports (see below)
 *
 * @implements: There is no AJava literature
 */
component WrongPortUsage {
  
  port
    in String input,
    out String output;
  
  compute PrintInput {
    System.out.println(sIn);
    String buffer = output;   // Cannot read from outgoing ports
    int bubu = 0;             // Port does not exist
    input = bubu;             // Cannot assign values to incoming ports
  }
}
