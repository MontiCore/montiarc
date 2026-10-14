/* (c) https://github.com/MontiCore/monticore */
package car;

component Garage {
  feature body, keylessEntry, powerLocks;
  feature engine, electric, gas, gear, manual, automatic;
  CarV2 car1, car2, car3, car4;
  CarV1 car5, car6;

  constraint (body == car1.body);
  constraint (body == car2.body);
  constraint (body == car3.body);
  constraint (body == car4.body);
  constraint (body == car5.body);
  constraint (body == car6.body);

  constraint (keylessEntry == car1.keylessEntry);
  constraint (keylessEntry == car2.keylessEntry);
  constraint (keylessEntry == car3.keylessEntry);
  constraint (keylessEntry == car4.keylessEntry);
  constraint (keylessEntry == car5.keylessEntry);
  constraint (keylessEntry == car6.keylessEntry);

  constraint (powerLocks == car1.powerLocks);
  constraint (powerLocks == car2.powerLocks);
  constraint (powerLocks == car3.powerLocks);
  constraint (powerLocks == car4.powerLocks);
  constraint (powerLocks == car5.powerLocks);
  constraint (powerLocks == car6.powerLocks);

  constraint (engine == car5.engine);
  constraint (engine == car6.engine);

  constraint (electric == car1.electric);
  constraint (electric == car2.electric);
  constraint (electric == car3.electric);
  constraint (electric == car4.electric);
  constraint (electric == car5.electric);
  constraint (electric == car6.electric);

  constraint (gas == car1.gas);
  constraint (gas == car2.gas);
  constraint (gas == car3.gas);
  constraint (gas == car4.gas);
  constraint (gas == car5.gas);
  constraint (gas == car6.gas);

  constraint (gear == car5.gear);
  constraint (gear == car6.gear);

  constraint (manual == car1.manual);
  constraint (manual == car2.manual);
  constraint (manual == car3.manual);
  constraint (manual == car4.manual);
  constraint (manual == car5.manual);
  constraint (manual == car6.manual);

  constraint (automatic == car1.automatic);
  constraint (automatic == car2.automatic);
  constraint (automatic == car3.automatic);
  constraint (automatic == car4.automatic);
  constraint (automatic == car5.automatic);
  constraint (automatic == car6.automatic);
}
