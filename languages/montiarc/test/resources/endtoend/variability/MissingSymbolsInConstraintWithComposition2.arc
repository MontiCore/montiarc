/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Valid model, however, the external component MissingSymbolsInConstraint1,
 * which defines the type of subcomponent sub, has a constraint that references
 * a missing symbol.
 */
component MissingSymbolsInConstraintWithComposition2 {

  feature f;

  MissingSymbolsInConstraint1 sub;

  constraint(f);

}
