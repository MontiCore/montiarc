/* (c) https://github.com/MontiCore/monticore */
package montiarc.features;

component CarV4 {
  // Declare the Features
  feature body, keylessEntry, powerLocks;

  // Add Nesting
  Gear gear;
  Engine engine;

  // Define Constraints
  constraint (body);
  constraint (!keylessEntry || powerLocks);

  // If we have an electric car, we also want to have a battery manufacturer
  // If it is not electric, we must choose between diesel or petrol
  if (electric) {
    feature batteryManufacturer;
    constraint (batteryManufacturer);
  } else {
    feature diesel, petrol;
    constraint ((diesel || petrol) && !(diesel && petrol));
  }
}
