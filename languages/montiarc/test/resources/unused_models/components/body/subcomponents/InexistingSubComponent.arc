/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

/**
 * Invalid model.
 * Component type 'Inexisting' does not exist.
 *
 * @implements [Hab16] R3: Full qualified subcomponent types exist in the
 *  named package. (p. 63, Lst. 3.38)
 * @implements [Hab16] R4: Unqualified subcomponent types either exist in
 *  the current package or are imported using an import statement.
 *  (p. 64, Lst. 3.39)
 */
component InexistingSubComponent {
  component Inexisting na;
  
  component de.ma.Delay delay;
}
