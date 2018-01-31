package component.body.ajava;

/**
 * Invalid model. Port 'counter' does not exist.
 */
component UsingInexistingPort {
  port
    in double distance,
    out String hulu;

  compute IncreaseHulu {    
    counter++;
    hulu = counter;
  }
}