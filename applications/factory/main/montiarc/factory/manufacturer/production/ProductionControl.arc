/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer.production;

import factory.Factory.*;

component ProductionControl {
  port in Material material,
       in ProductionOrder productionOrder,
       in Gear finishedGear;
  port out GearBlank gearBlank,
       out ProductConfig drillingConfig,
       out ProductConfig millingConfig,
       out ProductConfig heatingConfig,
       out ProductConfig grindingConfig,
       out ProductConfig polishingConfig,
       out Product product;
}