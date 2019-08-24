/* (c) https://github.com/MontiCore/monticore */
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
