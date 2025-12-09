/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: The symbol 'e' referenced in the constraint of the inner
 * component Inner is missing (the symbol cannot be resolved). Even though
 * constraints are composed alongside subcomponent composition, the constraint
 * of the outer component should be valid (hence ignore the invalid constraint).
 */
component MissingSymbolsInConstraintWithComposition1 {

  feature f;

  component Inner {
    constraint(e);
  }

  Inner sub;

  constraint(f);

}
