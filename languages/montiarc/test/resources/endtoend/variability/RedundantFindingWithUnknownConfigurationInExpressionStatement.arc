/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: The value 'true' cannot be assigned to 'x' for any variant.
 * The error should only be reported once, although the presence of the second
 * or third declaration depends on an unknown value.
 */
component RedundantFindingWithUnknownConfigurationInExpressionStatement(String s) {
  feature f;

  varif (f) {
    int x = 1;
  } else varif (s == "a") {
    int x = 2;
  } else {
    int x = 3;
  }

  compute {
    x = true;
  }
}
