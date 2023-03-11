/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Invalid model.
 */
component InvalidPortsUniqueSender {
  feature f1, f2;

  port in int pIn;
  A a;

  if (f1) {
    if (f2) {
      pIn -> a.pIn;
    }
  }
  if (f1 || f2) {
    pIn -> a.pIn; // duplicate when f1 && f2
  }

  if (!(f1 || f2)) {
    pIn -> a.pIn;
  }

  component A {
    port in int pIn;
  }
}
