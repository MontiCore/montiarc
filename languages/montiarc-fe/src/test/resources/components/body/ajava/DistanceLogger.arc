package components.body.ajava;

/**
 * Invalid model.
 * TODO Add test
 */
component DistanceLogger {
  port
    in double distance,
    out String hulu;

  compute increaseHulu {    
    distance++;
    hulu = distance;
  }
}