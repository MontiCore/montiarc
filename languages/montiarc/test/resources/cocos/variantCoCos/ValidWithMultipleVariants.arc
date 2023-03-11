/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Valid model.
 */
component ValidWithMultipleVariants(int i) {
  feature f;
  if (f) {
    port out boolean pOut;
  }
  if (i > 0) {
    port in int pInInt;
    if (f) {
      port in boolean pIn;
      port out int pOutInt;
      pIn -> pOut;
      pInInt -> pOutInt;
    } else {
      port out int pOut;
      pInInt -> pOut;
    }
  }
  if (f && i < 0) {
    B b;
    b.pOut -> pOut;
  }

  component B {
    port out boolean pOut;
  }

  component A a {}

  constraint (i != 0);
}
