/* (c) https://github.com/MontiCore/monticore */
package components.body.ajava;

/**
 * Invalid model. Port 'counter' does not exist.
 *
 * @implements AJava CoCo. No literature
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
