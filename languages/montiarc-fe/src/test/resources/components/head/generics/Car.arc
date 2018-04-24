package components.head.generics;

import com.google.common.collect.HashBasedTable;

/**
 * Valid model. It generates generic Ports with full qualified Names, #
 * Arrays, imports and sub-Generics 
 */
component Car<X>(X xParameter) {
  port 
  	in List<java.util.HashMap<Boolean,Double>[]> wheels,
  	out java.util.HashMap<Boolean,Double[]>[] motor;
  	
  java.util.List<X> xList; 
}