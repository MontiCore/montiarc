/* (c) https://github.com/MontiCore/monticore */
package constraintEvaluation;

/*
 * Invalid model.
 */
component NeverSatisfiedNestedInstanceConstraints {
  component A(int i) {
    ComponentWithBooleanParameterAsConstraint c(i < 0);
  }

  A a(0);
}
