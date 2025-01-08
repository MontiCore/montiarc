/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer;

import factory.Factory.*;

component Dispatch {
  port in Product product,
       in SalesOrder salesOrder;
  port <<delayed>> out Shipping shipping,
       <<delayed>> out ShippingConfirmation shippingConfirmation;
}