/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer;

import factory.Factory.*;

component Sales {
  port in Inquiry inquiry,
       in OrderConfirmation orderConfirmation,
       in ShippingConfirmation shippingConfirmationIn;
  port out OrderQuotation orderQuotation,
       out SalesOrder salesOrder4Production,
       out SalesOrder salesOrder4Procurement,
       out SalesOrder salesOrder4Dispatch,
       out ShippingConfirmation shippingConfirmationOut;

  <<delayed>> compute {}
}