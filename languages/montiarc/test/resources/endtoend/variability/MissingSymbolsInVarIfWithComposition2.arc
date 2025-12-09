/* (c) https://github.com/MontiCore/monticore */
package variability;

/*
 * Valid model, however, the external component MissingSymbolsInVarif4,
 * which defines the type of subcomponent sub, has a varif statement that
 * references a missing symbol. Even though variation points are composed
 * alongside subcomponent composition, the varif statement of the outer
 * component should be valid (hence the invalid varif statement should be ignored).
 */
component MissingSymbolsInVarIfWithComposition2 {

  feature f;

  MissingSymbolsInVarIf1 sub;

  varif(f) { }

}
