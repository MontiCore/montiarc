/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Valid model.
 */
component ValidWithComposedVariants {
  component A(int i) {
    if (i == 0) {
      port out boolean pOut;
    } else if (i == 2) {
      port out int pOut;
    }
  }

  port out int pOut;
  A a(2);
  a.pOut -> pOut;
}
