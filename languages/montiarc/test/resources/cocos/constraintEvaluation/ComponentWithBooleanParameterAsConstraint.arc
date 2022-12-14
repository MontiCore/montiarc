/* (c) https://github.com/MontiCore/monticore */
package constraintEvaluation;

/*
 * Valid model.
 */
component ComponentWithBooleanParameterAsConstraint(boolean b) {
  constraint(b);
}