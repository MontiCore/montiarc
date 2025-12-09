/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: The symbol 'e' referenced in the condition of the varif
 * statement of the inner component 'Inner' is missing (the symbol cannot be
 * resolved).
 */
component MissingSymbolsInVarIf3 {

  component Inner {
    varif(e) { }
  }

}
