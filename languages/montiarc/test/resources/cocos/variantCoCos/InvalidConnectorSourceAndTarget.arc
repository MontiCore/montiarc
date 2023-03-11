/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Invalid model.
 */
component InvalidConnectorSourceAndTarget(int i) {
  feature f;
  // ConnectorSourceAndTargetDirectionsFit
  // ConnectorSourceAndTargetExist
  // ConnectorSourceAndTargetTypesFit

  A a(i);

  if (f) {
    port in int pIn;
  } else {
    port in double pIn;
  }

  pIn -> a.p; // OK, Wrong direction, Wrong direction + Wrong type, Wrong type, or 2*Missing

  component A(int i) {
    if (i < 0) {
      port in int p;
    }
    if (i > 0) {
      port out int p;
    }
  }
}
