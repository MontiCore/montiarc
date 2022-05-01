/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExist;

/*
 * Invalid model.
 */
component TargetDoesNotExist {
  port in String sIn;

  sIn -> missingPort; // Error, target of direct port-forward does not exist

  component Inner {
    port out String sOut;
  }

  Inner inner1, inner2;

  sIn -> inner1.missingPort; // Error, target of incoming port-forward does not exist
  inner1.sOut -> inner2.missingPort; // Error, target of hidden channel does not exist
  inner2.sOut -> missingPort; // Error, target of outgoing port-forward does not exist

  sIn -> noInner.sIn; // Error, as noInner is no existing subcomponent.
}