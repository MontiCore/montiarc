/* (c) https://github.com/MontiCore/monticore */

tags StreamBoiler {
  tag mceffect.steamboiler.SteamBoiler.waterIn with check_potential_effect = "waterOut";
  tag mceffect.steamboiler.SteamBoiler.waterIn with check_no_effect = "status";
  tag mceffect.steamboiler.SteamBoiler.heat with check_no_effect = "waterOut";

  tag mceffect.steamboiler.WaterTank.waterIn with check_mandatory_effect = "waterOut";

  tag mceffect.steamboiler.Heater.waterIn with check_mandatory_effect = "waterOut";
  tag mceffect.steamboiler.Heater.heat with check_mandatory_effect = "waterOut";

  tag mceffect.steamboiler.Controller.waterLevel with check_mandatory_effect = "signal";
}