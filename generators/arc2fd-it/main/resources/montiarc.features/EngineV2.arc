/* (c) https://github.com/MontiCore/monticore */
package montiarc.features;

component EngineV2 {
  feature electric, gas;

  // If we have an electric car, we also want to have a battery manufacturer
  // If it is not electric, we must choose between diesel or petrol
  if (electric) {
    feature batteryManufacturer;
    constraint (batteryManufacturer);
  } else {
    feature diesel, petrol;
    constraint ((diesel || petrol) && !(diesel && petrol));
  }

  // Constraint for "Electric or Gas"
  constraint (electric || gas);
}
