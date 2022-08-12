/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetTimingsFit;

/*
 * Valid model.
 */
component SourceAndTargetTimingsFit {
  port <<sync>> in int sIn;
  port <<instant>> out int sOut;

  component Inner {
    port <<sync>> in int sIn;
    port <<delayed>> out int sOut;
  }

  Inner inner1;

  sIn -> inner1.sIn;
  inner1.sOut -> sOut;
}