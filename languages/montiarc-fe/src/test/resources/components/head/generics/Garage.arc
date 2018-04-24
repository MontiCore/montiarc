package components.head.generics;

/**
 * Valid model. 
 */
component Garage {

  port 
    in List<java.util.HashMap<Boolean,Double>[]> wheels,
    out java.util.HashMap<Boolean,Double[]>[] motor;

  component Car<String>("My awesome car");
  
  connect wheels -> car.wheels;
  connect car.motor -> motor;
}