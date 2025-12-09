/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: The symbol 'e' referenced in the condition of the varif
 * statement of the inner component 'Inner' is missing (the symbol cannot be
 * resolved). Even though variation points are composed alongside subcomponent
 * composition, the varif statement of the outer component should be valid
 * (hence the invalid varif statement should be ignored).
 */
component MissingSymbolsInVarIfWithComposition1 {

  feature f;

  component Inner {
    varif(e) { }
  }

  Inner sub;

  varif(f) { }

}
