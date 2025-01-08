/* (c) https://github.com/MontiCore/monticore */
package factory.environment;

import factory.Factory.*;

component Supplier {
  /* Assuming that manufacturer can order and material is delivered without bureaucracy */
  port in PurchaseOrder purchaseOrder;
  port <<delayed>> out Material material;
}