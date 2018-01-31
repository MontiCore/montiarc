package components.head.generics;

/**
 * Valid model. It generates generic Ports with full qualified Names, Arrays, imports and sub-Generics 
 */
component Car<X>(X xParameter) {
  port 
  	in List<com.google.common.collect.ImmutableMap<Boolean,Double>[]> wheels,
  	out com.google.common.collect.HashBasedTable<Boolean,Double[],List<String>>[] motor;
  
  component Motor<X>(xParameter) m;
  connect wheels -> m.wheels;
  connect m.motor -> motor;
}