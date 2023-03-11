/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Invalid model.
 */
component InvalidUniqueIdentifiersNames(int i) {
  port in int pIn; // Not connected once


  if (i > 0) {
    port in int pIn; // 2*Not unique
    port out int pOut;

    pIn -> pOut;
  }
  if (i > 5) {
    port out int pOut; // Not unique
    A a(); // Not Unique
  }

  component A a {}
}
