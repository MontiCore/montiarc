/* (c) https://github.com/MontiCore/monticore */
package montiarc.features;

component CarV2 {
  // Declare the Features
  feature body, keylessEntry, powerLocks;

  // Add Nesting
  Gear gear;
  Engine engine;

  // Define Constraints
  constraint (body);
  constraint (!keylessEntry || powerLocks);
}
