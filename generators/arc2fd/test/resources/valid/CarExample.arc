/* (c) https://github.com/MontiCore/monticore */

component CarExample {

  feature car, body, engine, gear, manual, automatic, electric, gas, powerLocks, keylessEntry;

  // children require their parents
  constraint ((!electric || engine) && (!gas || engine) && (!manual || gear) && (!automatic || gear));

  // mandatory features
  constraint (car && body && engine && gear);

  // or-group
  constraint (!engine || (electric || gas));

  // xor-group
  constraint (!gear || ((manual || automatic) && !(manual && automatic)));

  // optional feature with an optional child
  constraint (!keylessEntry || powerLocks);
}
