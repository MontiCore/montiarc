/* (c) https://github.com/MontiCore/monticore */

tags StreamBoiler {
  tag steamboiler.SteamBoiler.waterIn with check_potential_effect = "waterOut";
  tag steamboiler.SteamBoiler.waterIn with check_no_effect = "status";
  tag steamboiler.SteamBoiler.heat with check_no_effect = "waterOut";

  tag steamboiler.WaterTank.waterIn with check_mandatory_effect = "waterOut";

  tag steamboiler.Heater.waterIn with check_mandatory_effect = "waterOut";
  tag steamboiler.Heater.heat with check_mandatory_effect = "waterOut";

  tag steamboiler.Controller.waterLevel with check_mandatory_effect = "signal";
}