/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: The symbols 'e1' and 'e2' referenced in the two constraints
 * are missing (the symbol cannot be resolved).
 */
component MissingSymbolsInConstraint2 {

  constraint(e1);
  constraint(e2);

}
