package components.head.generics;

/**
 * Valid model. 
 */
component Garage {

  port 
    in List<com.google.common.collect.ImmutableMap<Boolean,Double>[]> wheels,
    out HashBasedTable<Boolean,Double[],List<String>>[] motor;

  component Car<String>("My awesome car");
  
  connect wheels -> car.wheels;
  connect car.motor -> motor;
}