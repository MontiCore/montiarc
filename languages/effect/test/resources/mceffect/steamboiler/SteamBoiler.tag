/* (c) https://github.com/MontiCore/monticore */

tags StreamBoiler {
  tag mceffect.steamboiler.SteamBoiler.waterIn with potential_effect = "waterOut";
  tag mceffect.steamboiler.SteamBoiler.waterIn with no_effect = "status";
  tag mceffect.steamboiler.SteamBoiler.heat with no_effect = "waterOut";

  tag mceffect.steamboiler.WaterTank.waterIn with mandatory_effect = "waterOut";

  tag mceffect.steamboiler.WaterTank.waterIn with check_mandatory_effect = "waterOut";
  tag mceffect.steamboiler.WaterTank.heat with check_mandatory_effect = "waterOut";

  tag mceffect.steamboiler.Controller.waterLevel with check_mandatory_effect = "signal";
}