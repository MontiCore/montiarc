/* (c) https://github.com/MontiCore/monticore */
package factory.company;

import factory.Factory.*;

component SalesManager {
  port in Order order;
  port out Invoice invoice,
       out Offer offer;

  automaton {
    // TODO: Define product pricing, send offers and invoices
    initial state s;
  }


}