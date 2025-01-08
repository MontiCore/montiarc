/* (c) https://github.com/MontiCore/monticore */
package factory.environment;

import factory.Factory.*;

component Customer {
  port in OrderQuotation quotation,
       in Shipping shipping,
       in ShippingConfirmation shippingConfirmation;
  port <<delayed>> out Inquiry inquiry,
       <<delayed>> out OrderConfirmation orderConfirmation;
}