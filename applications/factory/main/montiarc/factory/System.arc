/* (c) https://github.com/MontiCore/monticore */
package factory;

import factory.manufacturer.Manufacturer;
import factory.environment.Supplier;
import factory.environment.Customer;

component System {
  Customer customer;
  Manufacturer manufacturer;
  Supplier supplier;

  customer.inquiry -> manufacturer.inquiry;
  customer.orderConfirmation -> manufacturer.orderConfirmation;
  manufacturer.orderQuotation -> customer.quotation;
  manufacturer.shipping -> customer.shipping;
  manufacturer.shippingConfirmation -> customer.shippingConfirmation;

  supplier.material -> manufacturer.material;
  manufacturer.purchaseOrder -> supplier.purchaseOrder;
}
