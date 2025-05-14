/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer;

import factory.Factory.*;

component Procurement {
  port in SalesOrder salesOrder,
       in Inventory inventory;
  port out PurchaseOrder purchaseOrder;

  <<delayed>> compute {}
}