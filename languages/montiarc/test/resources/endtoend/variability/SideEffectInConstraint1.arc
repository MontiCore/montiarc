/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: Assignment expressions (or in general expressions with side
 * effects) are not allowed in constraints.
 */
component SideEffectInConstraint1(boolean p) {
  constraint (p = true);
}
