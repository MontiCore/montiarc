/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer;

import factory.Factory.*;

component Dispatch {
  port in Product product,
       in SalesOrder salesOrder;
  port out Shipping shipping,
       out ShippingConfirmation shippingConfirmation;

  <<delayed>> compute {}
}