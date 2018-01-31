package components.head.generics;

/**
 * Valid model. It generates generic Ports with full qualified Names, Arrays, imports and sub-Generics 
 */
component Garage {
  component Car<String>("My awesome car");
}