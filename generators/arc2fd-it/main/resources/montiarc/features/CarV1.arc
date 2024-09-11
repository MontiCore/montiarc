/* (c) https://github.com/MontiCore/monticore */
package montiarc.features;

component CarV1 {
  // Declare the Features
  feature body, keylessEntry, powerLocks;
  feature engine, electric, gas, gear, manual, automatic;

  // Define Constraints
  constraint (body);
  constraint (engine && (electric || gas));
  constraint (gear && (manual || automatic) && !(manual && automatic));
  constraint (keylessEntry || !keylessEntry);
  constraint (powerLocks || !powerLocks);
  constraint (!keylessEntry || powerLocks);
}
