/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Valid model: The constraint depends on an unknown value, so it is satisfied
 * for some values of 's' but not for others. This should raise a warning.
 */
component UnknownValueInConstraint(String s) {
  constraint (s == "a");
}
