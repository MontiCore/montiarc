/* (c) https://github.com/MontiCore/monticore */
package lighting;

component EnlightenedCar {
  timing sync;

  port out String doorLight;
  port out String mirrorLight;

  LightCtrl control;
  RandomBlinker door;
  RandomBlinker user;

  door.output -> control.doorOpen;
  user.output -> control.changeMode;

  control.door -> doorLight;
  control.mirror -> mirrorLight;
}
