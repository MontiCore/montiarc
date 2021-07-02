/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetExistAndFit;

/*
 * Invalid model.
 */
component SourceAndTargetDoNotExist {

  missingSource -> missingTarget; // Error, source and target of direct port-forward do not exist

  component Inner {
    port out String sOut;
  }

  Inner inner1, inner2;

  missingSource -> inner1.missingTarget; // Error, source and target of incoming port-forward do not exist
  inner1.missingSource -> inner2.missingTarget; // Error, source and target of hidden channel do not exist
  inner2.missingSource -> missingTarget; // Error, source and target of outgoing port-forward do not exist
}