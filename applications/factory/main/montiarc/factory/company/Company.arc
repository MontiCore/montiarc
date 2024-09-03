/* (c) https://github.com/MontiCore/monticore */
package factory.company;

import factory.Factory.*;
import factory.environment.Supplier;
import factory.environment.Customer;

component Company {
  port in Money income,
       in Order order,
       in Material material,
       in Invoice supplierInvoice;
  port <<delayed>> out ConstructionPart producedPart,
       <<delayed>> out Invoice customerInvoice,
       out PurchaseOrder purchase,
       out Offer offer,
       <<delayed>> out Money expenses;

  Warehouse warehouse;
  Production production;

  FinanceManager finances;
  ProcurementManager procurement;
  SalesManager sales;

  /* IN */
  income -> finances.income;
  order -> warehouse.order;
  order -> production.order;
  order -> sales.order;
  material -> warehouse.materialIn;
  supplierInvoice -> finances.invoice;

  /* OUT */
  production.producedPart -> producedPart;
  finances.capital -> expenses;
  procurement.purchase -> purchase;
  sales.invoice -> customerInvoice;
  sales.offer -> offer;

  /* Other connections */
  warehouse.materialOut -> production.material;
  warehouse.materialOut -> procurement.material;
}