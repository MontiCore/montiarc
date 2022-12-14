/* (c) https://github.com/MontiCore/monticore */
package constraintEvaluation;

/*
 * Invalid model.
 */
component NeverSatisfiedInstanceConstraints {
  ComponentWithBooleanParameterAsConstraint c(false);
}