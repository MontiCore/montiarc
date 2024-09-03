/* (c) https://github.com/MontiCore/monticore */
package factory.environment;

import factory.Factory.*;

component Customer {
  port in ConstructionPart producedPart,
       in Offer offer,
       in Invoice invoice;
  port <<delayed>> out Order order,
       <<delayed>> out Money payment;
}