/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Invalid model.
 */
component InvalidSubPortsConnected {
  feature f1, f2;

  A a;

  if (f1) { // 3x: a.pIn unconnected when !f1
    port in int pIn;
    pIn -> a.pIn;
  }

  if (f2) { // 2x: a.pOut unconnected when !f2
    port out int pOut;
    a.pOut -> pOut;
  }

  constraint (!f2 || a.f);

  component A() {
    feature f;
    port in int pIn;
    if (f) {
      port out int pOut;
    }
  }
}
