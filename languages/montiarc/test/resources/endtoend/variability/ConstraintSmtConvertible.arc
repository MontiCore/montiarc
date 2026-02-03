/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: The constraint cannot be converted to smt as it contains object and method references.
 */
component ConstraintSmtConvertible(java.util.Optional<Object> obj) {
  constraint(obj.isPresent());
}
