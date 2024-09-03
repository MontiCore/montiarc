/* (c) https://github.com/MontiCore/monticore */
package factory;

import factory.company.Company;
import factory.environment.Supplier;
import factory.environment.Customer;

component System {
  Company company;
  Customer customer;
  Supplier supplier;

  company.producedPart -> customer.producedPart;
  company.customerInvoice -> customer.invoice;
  company.purchase -> supplier.purchase;
  company.expenses -> supplier.costs;

  customer.payment -> company.income;
  customer.order -> company.order;
  company.offer -> customer.offer;

  supplier.material -> company.material;
  supplier.invoice -> company.supplierInvoice;
}