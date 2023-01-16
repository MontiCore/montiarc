/* (c) https://github.com/MontiCore/monticore */
package constraintEvaluation;

/*
 * Invalid model.
 */
component NeverSatisfiedTransitiveInstanceConstraints(boolean b) {
  ComponentWithBooleanParameterAsConstraint c(b);
  constraint(!b);
}