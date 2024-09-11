/* (c) https://github.com/MontiCore/monticore */
package montiarc.features;

component CarV3 {
  // Declare the Features
  feature body, keylessEntry, powerLocks;

  // Add Nesting
  Gear gear;
  EngineV2 engine;

  // Define Constraints
  constraint (body);
  constraint (!keylessEntry || powerLocks);
}
