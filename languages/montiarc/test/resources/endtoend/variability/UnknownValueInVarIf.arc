/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Valid model: The port 'o' has two senders if the connector in the varif is
 * present, which depends on an unknown value. This should raise a warning.
 */
component UnknownValueInVarIf(String s) {
  port out int o, p;

  component Inner {
    port out int o;
  }

  Inner a, b;

  a.o -> o;
  b.o -> p;

  varif (s == "a") {
    b.o -> o;
  }
}
