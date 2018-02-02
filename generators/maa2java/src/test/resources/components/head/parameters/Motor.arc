package components.head.parameters;

/**
 * Valid model. It generates generic Ports with full qualified Names, Arrays, imports and sub-Generics 
 */
component Motor<X>(X xParameter) {
  port 
  	in List<com.google.common.collect.ImmutableMap<Boolean,Double>[]> wheels,
  	out com.google.common.collect.HashBasedTable<Boolean,Double[],List<String>>[] motor;
  	
  java.util.List<X> xList; 
}