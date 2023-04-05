/* (c) https://github.com/MontiCore/monticore */
package missingVariable;

/*
 * Invalid model. The symbol f in the constraint is missing.
 */
component MissingVariableInConstraint {
  constraint(f == 1);
}
