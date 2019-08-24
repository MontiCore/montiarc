/* (c) https://github.com/MontiCore/monticore */
package components.head.generics;

/**
 * Valid model. 
 */
component Garage {

  port 
    in List<java.util.HashMap<Boolean,Double>> wheels,
    out java.util.HashMap<Double[],List<String>> motor;

  component Car<String>("My awesome car");
  
  connect wheels -> car.wheels;
  connect car.motor -> motor;
}
