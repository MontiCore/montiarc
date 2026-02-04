/* (c) https://github.com/MontiCore/monticore */
package modes;

/**
 * Invalid model: A mode contains a port definition.
 */
component PortInMode1 {

  mode automaton {
    initial mode m1 {
      port in int pIn;
    }
  }
}
