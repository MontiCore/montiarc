/* (c) https://github.com/MontiCore/monticore */
package constraintEvaluation;

/*
 * Invalid model.
 */
component NeverSatisfiedInstanceFeatureConstraints {
  ComponentWithFeatureAsConstraint c;
  constraint(!c.f);
}