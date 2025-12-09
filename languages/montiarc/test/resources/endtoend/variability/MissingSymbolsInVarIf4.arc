/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Invalid model: The symbols 'e1', 'e2', and 'e3' referenced in the conditions
 * of various varif statements are missing (the symbols cannot be resolved).
 */
component MissingSymbolsInVarIf4 {

  varif(e1) {
    varif(e2) { }
  } else varif(e3) { }

}
