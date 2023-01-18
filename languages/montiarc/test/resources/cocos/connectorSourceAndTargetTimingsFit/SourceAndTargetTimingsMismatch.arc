/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetTimingsFit;

/*
 * Invalid model.
 */
component SourceAndTargetTimingsMismatch {
  port <<sync>> in int sIn;
  port <<delayed>> out int sOut;

  sIn -> sOut; // Error, sync -> delayed

  component Inner {
    port in int sIn;
    port <<causalsync>> out int sOut;

    sIn -> sOut; // Error, untimed -> causalsync
  }

  Inner inner1;

  sIn -> inner1.sIn; // Error, sync -> untimed
  inner1.sOut -> sOut; // Error, causalsync -> delayed
}
