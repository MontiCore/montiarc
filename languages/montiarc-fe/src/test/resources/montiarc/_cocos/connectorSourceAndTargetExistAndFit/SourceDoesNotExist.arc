/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExistAndFit;

/*
 * Invalid model.
 */
component SourceDoesNotExist {
  port out String sOut;

  missingPort -> sOut; // Error, source of direct port-forward does not exist

  component Inner {
    port in String sIn;
  }

  Inner inner1, inner2;

  missingPort -> inner1.sIn; // Error, source of incoming port-forward does not exist
  inner1.missingPort -> inner2.sIn; // Error, source of hidden channel does not exist
  inner2.missingPort -> sOut; // Error, source of outgoing port-forward does not exist
}