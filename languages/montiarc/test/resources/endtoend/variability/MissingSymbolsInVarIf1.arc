/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: The symbol 'e' referenced in the condition of the varif
 * statement is missing (the symbol cannot be resolved).
 */
component MissingSymbolsInVarIf1 {

  varif(e) { }

}
