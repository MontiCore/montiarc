package contextconditions.valid;

component DistanceLogger {
  port
    in double distance,
    out String hulu;

  compute increaseHulu {    
    distance++;
    hulu = distance;
  }
}