package components.body.ajava;

/**
 * Invalid model.
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