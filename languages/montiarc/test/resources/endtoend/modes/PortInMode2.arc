/* (c) https://github.com/MontiCore/monticore */
package modes;

/**
 * Invalid model: Multiple modes contain port definitions.
 */
component PortInMode2 {

  mode automaton {
    initial mode m1 {
      port in int pIn;
    }
    mode m2 {
      port out double pOut;
    }
  }
}
