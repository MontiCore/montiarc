/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: Assignment expressions are not allowed inside constraints.
 */
component ConstraintNoAssignmentExpression(boolean p) {
  constraint(p = true);
}
