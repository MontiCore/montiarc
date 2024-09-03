/* (c) https://github.com/MontiCore/monticore */
package factory.environment;

import factory.Factory.*;

component Supplier {
  port in PurchaseOrder purchase,
       in Money costs;
  port <<delayed>> out Material material,
       <<delayed>> out Invoice invoice;

  automaton {
    // TODO: Send invoices and material depending on purchase orders
    initial state s;
  }

}