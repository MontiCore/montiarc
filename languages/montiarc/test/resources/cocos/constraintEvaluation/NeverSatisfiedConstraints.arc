/* (c) https://github.com/MontiCore/monticore */
package constraintEvaluation;

/*
 * Invalid model.
 */
component NeverSatisfiedConstraints {
  constraint(false);
  constraint(true == false);
  constraint(1 == 2);
  constraint(1 + 2 == 5 * 2);
  constraint(true);
}