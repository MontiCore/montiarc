/* (c) https://github.com/MontiCore/monticore */
package constraintEvaluation;

/*
 * Invalid model.
 */
component NeverSatisfiedConstraintsWithParameter(int p1, int p2) {
  constraint(true);
  constraint(p1 < 0);
  constraint(p1 + 1 == p2);
  constraint(p2 > 0);
}
