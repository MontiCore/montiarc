/* (c) https://github.com/MontiCore/monticore */
package constraintEvaluation;

/*
 * Valid model.
 */
component AllConstraintsSatisfiable {
  constraint(true);
  constraint(true == true);
  constraint(1 == 1);
  constraint(2 + 8 == 5 * 2);
}