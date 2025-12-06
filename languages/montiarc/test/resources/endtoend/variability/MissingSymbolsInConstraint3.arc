/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: The symbol 'e' referenced in the constraint of the inner
 * component Inner is missing (the symbol cannot be resolved).
 */
component MissingSymbolsInConstraint3 {

  component Inner {
    constraint(e);
  }

}
