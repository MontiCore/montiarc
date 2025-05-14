/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer;

import factory.Factory.*;
import factory.environment.Supplier;
import factory.environment.Customer;
import factory.manufacturer.production.ProductionScheduling;
import factory.manufacturer.production.Production;

component Manufacturer {
  port in Inquiry inquiry,
       in OrderConfirmation orderConfirmation,
       in Material material;
  port out OrderQuotation orderQuotation,
       out Shipping shipping,
       out ShippingConfirmation shippingConfirmation,
       out PurchaseOrder purchaseOrder;

  Sales sales;
  Storage storage;
  Procurement procurement;
  ProductionScheduling productionScheduling;
  Production production;
  Dispatch dispatch;
  /* Financial aspect currently not considered */

  inquiry -> sales.inquiry;
  orderConfirmation -> sales.orderConfirmation;
  material -> storage.materialIn;

  // Sales
  sales.orderQuotation -> orderQuotation;
  sales.shippingConfirmationOut -> shippingConfirmation;
  sales.salesOrder4Production -> productionScheduling.salesOrder;
  sales.salesOrder4Procurement -> procurement.salesOrder;
  sales.salesOrder4Dispatch -> dispatch.salesOrder;

  // Storage
  storage.materialOut -> production.material;
  storage.inventory -> procurement.inventory;

  // Procurement
  procurement.purchaseOrder -> purchaseOrder;

  // Production Scheduling
  productionScheduling.productionOrder -> production.productionOrder;

  // Production
  production.materialRequest -> storage.materialRequest;
  production.product -> dispatch.product;

  // Dispatch
  dispatch.shipping -> shipping;
  dispatch.shippingConfirmation -> sales.shippingConfirmationIn;
}