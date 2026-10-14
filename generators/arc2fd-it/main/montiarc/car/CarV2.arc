/* (c) https://github.com/MontiCore/monticore */
package car;

component CarV2 {
  feature body, keylessEntry, powerLocks;
  feature manual, automatic, electric, gas;

  Gear gear;
  Engine engine;

  constraint (body);
  constraint (!keylessEntry || powerLocks);
  constraint (manual == gear.manual);
  constraint (automatic == gear.automatic);
  constraint (electric == engine.electric);
  constraint (gas == engine.gas);
}
