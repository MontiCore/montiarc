/* (c) https://github.com/MontiCore/monticore */
package factory.company;

import factory.Factory.*;

component FinanceManager {
  port in Money income,
       in Invoice invoice;
  port out Money capital;

  automaton {
    // TODO: Bank account
    initial state s;
  }

}