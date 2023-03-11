/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Invalid model.
 */
component InvalidInheritedPortsTypeCorrect {
  component A (int i) {
    if (i < 0) {
      port in int p;
    }
    if (i == 0) {
      port out int p;
    }
    if (i > 0) {
      port in int p;
    }
  }

  component B (int i) extends A(i) {
    if (i < 0) {
      port in boolean p; // wrong type
    }
    if (i == 0) {
      port in int p; // wrong direction
    }
    if (i > 0) {
      port out boolean p; // wrong direction and type
    }
  }
}
