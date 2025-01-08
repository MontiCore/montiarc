/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer.production.lines;

import factory.Factory.*;

import factory.manufacturer.production.machines.*;

component GearProductionLine {
  port in GearBlank gearBlank,
       in ProductConfig drillingConfig,
       in ProductConfig millingConfig,
       in ProductConfig heatingConfig,
       in ProductConfig grindingConfig,
       in ProductConfig polishingConfig;
  port <<delayed>> out Gear finishedGear;

  Drilling<GearBlank, Gear> drilling;
  Milling<Gear, Gear> milling;
  Heating<Gear, Gear> heating;
  Grinding<Gear, Gear> grinding;
  Polishing<Gear, Gear> polishing;

  drillingConfig -> drilling.config;
  millingConfig -> milling.config;
  heatingConfig -> heating.config;
  grindingConfig -> grinding.config;
  polishingConfig -> polishing.config;

  gearBlank -> drilling.pieceIn;
  drilling.pieceOut -> milling.pieceIn;
  milling.pieceOut -> heating.pieceIn;
  heating.pieceOut -> grinding.pieceIn;
  grinding.pieceOut -> polishing.pieceIn;
  polishing.pieceOut -> finishedGear;
}