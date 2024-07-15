/* (c) https://github.com/MontiCore/monticore */
package mceffect.steamboiler;

import Datatypes.*;

component SteamBoiler{

  port in double heat,
       in double waterIn;

  port out double waterOut,
       out boolean status;


  WaterTank tank;
  Heater heater;
  Controller controller;


  waterIn -> tank.waterIn;
  tank.waterOut -> heater.waterIn;
  heat -> heater.heat;
  heater.waterOut -> waterOut;

  controller.signal -> tank.valveStatus ;
  tank.waterLevel -> controller.waterLevel;

}