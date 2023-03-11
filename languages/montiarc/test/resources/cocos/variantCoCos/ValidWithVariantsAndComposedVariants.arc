/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Valid model.
 */
component ValidWithVariantsAndComposedVariants(int i) {
  component A(int i) {
    if (i == 0) {
      port out boolean pOut;
    } else if (i == 2) {
      port out int pOut;
    }
  }

  if (i == 0) {
    port out boolean pOut;
    A a(i);
    a.pOut -> pOut;
  } else if (i == 4) {
    port out int pOut;
    A a(i - 2);
    a.pOut -> pOut;
  }

  A a2(i);
  if (i == 0 || i == 2) {
    port out int pOut2;
    a2.pOut -> pOut2;
  }
}
