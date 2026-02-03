/* (c) https://github.com/MontiCore/monticore */
package variability;

/**
 * Invalid model: Several identifiers have invalid casing.
 */
component NamesCapitalizationWithVariability {

  feature F;

  varif(F) {
    port in int I;
    int V = 0;
    component inner1<t>(int P) {
      port in int I;
    }
    inner1<int> Sub(0);
    I -> Sub.I;
  } else {
    port in int I;
    int V = 0;
    component inner2<t>(int P) {
      port in int I;
    }
    inner2<int> Sub(0);
    I -> Sub.I;
  }
}
