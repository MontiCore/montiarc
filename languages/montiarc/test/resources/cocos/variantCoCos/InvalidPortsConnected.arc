/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Invalid model.
 */
component InvalidPortsConnected {
  feature f1, f2;

  A a1(true); // pIn unconnected when !(f1 && f2)
  A a2(f2);
  A a3(f1); // pOut unconnected when !f2
  A a4(false); // 2x pIn unconnected when !f2

  port in int pIn;
  pIn -> a3.pIn;
  a1.pOut -> a2.pIn;

  if (f1 && f2) {
    a3.pOut -> a1.pIn;
  }

  if (f2) {
    a2.pOut -> a4.pIn;
  }

  component A(boolean b) {
    port in int pIn;
    if (b) {
      port out int pOut;
    }
  }
}
