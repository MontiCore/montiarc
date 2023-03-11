/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Invalid model.
 */
component InvalidUnderspecification {
  feature f;

  A a; // a.g is not set

  if (f) {
    port in int p;
    p -> a.p; // wrong direction when a.g
  } else {
    port out int p;
    a.p -> p; // wrong direction when not a.g
  }

  component A {
    feature g;

    if (g) {
      port in int p;
    } else {
      port out int p;
    }
  }
}
