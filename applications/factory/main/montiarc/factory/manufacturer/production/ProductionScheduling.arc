/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer.production;

import factory.Factory.*;

component ProductionScheduling {
  port in SalesOrder salesOrder;
  port out ProductionOrder productionOrder;
}