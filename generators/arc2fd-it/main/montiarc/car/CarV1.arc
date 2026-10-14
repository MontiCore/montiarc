/* (c) https://github.com/MontiCore/monticore */
package car;

component CarV1 {
  feature body, keylessEntry, powerLocks;
  feature engine, electric, gas, gear, manual, automatic;

  constraint (body);
  constraint (engine && (electric || gas));
  constraint (gear && (manual || automatic) && !(manual && automatic));
  constraint (keylessEntry || !keylessEntry);
  constraint (powerLocks || !powerLocks);
  constraint (!keylessEntry || powerLocks);
}
