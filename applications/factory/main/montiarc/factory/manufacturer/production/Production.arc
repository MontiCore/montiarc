/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer.production;

import factory.Factory.*;
import factory.manufacturer.production.lines.*;

component Production {
  port in ProductionOrder productionOrder,
       in Material material;
  port <<delayed>> out Product product,
       out MaterialRequest materialRequest;

  ProductionControl control;

  // Production lines
  GearProductionLine gearProductionLine;

  productionOrder -> control.productionOrder;
  material -> control.material;
  control.product -> product;

  control.gearBlank -> gearProductionLine.gearBlank;
  gearProductionLine.finishedGear -> control.finishedGear;
  control.drillingConfig -> gearProductionLine.drillingConfig;
  control.millingConfig -> gearProductionLine.millingConfig;
  control.heatingConfig -> gearProductionLine.heatingConfig;
  control.grindingConfig -> gearProductionLine.grindingConfig;
  control.polishingConfig -> gearProductionLine.polishingConfig;
}