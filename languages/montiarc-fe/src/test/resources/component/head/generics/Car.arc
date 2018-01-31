package component.head.generics;

import com.google.common.collect.HashBasedTable;

/**
 * Valid model. It generates generic Ports with full qualified Names, #
 * Arrays, imports and sub-Generics 
 */
component Car<X>(X xParameter) {
  port 
  	in List<com.google.common.collect.ImmutableMap<Boolean,Double>[]> wheels,
  	out HashBasedTable<Boolean,Double[],List<String>>[] motor;
  	
  java.util.List<X> xList; 
}