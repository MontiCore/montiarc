package components.head.generics;

/**
 * Valid model. It generates generic Ports with full qualified Names, Arrays, imports and sub-Generics 
 */
component Garage {
  port 
    in List<com.google.common.collect.ImmutableMap<Boolean,Double>[]> wheels,
    out com.google.common.collect.HashBasedTable<Boolean,Double[],List<String>>[] motor;

  component Car<String>("My awesome car");
  
  connect wheels -> car.wheels;
  connect car.motor -> motor;
}