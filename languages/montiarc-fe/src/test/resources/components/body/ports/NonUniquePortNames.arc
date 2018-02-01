package components.body.ports;

/**
 * Invalid model. The port names 'i', 'string', and 'object' are each
 * used twice.
 */
component NonUniquePortNames {
  port
    in Integer i,
    in String, 
    in Object object,
    out Integer i,
    out String,
    out Object;
  
  automaton {  
  	state Start;
    initial Start;

    Start -> Start;
  }
}
