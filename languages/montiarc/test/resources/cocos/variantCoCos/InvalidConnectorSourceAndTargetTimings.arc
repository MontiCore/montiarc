/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Invalid model.
 */
component InvalidConnectorSourceAndTargetTimings(int i) {
  feature f;

  A a(i);

  if (f) {
    port <<timed>> in int pIn;
  } else {
    port <<sync>> in int pIn;
  }

  pIn -> a.p; // 2x OK, 4x Wrong timing

  component A(int i) {
    if (i < 0) {
      port <<sync>> in int p;
    }
    if (i > 0) {
      port <<untimed>> in int p;
    }
    if (i == 0) {
      port <<timed>> in int p;
    }
  }
}
