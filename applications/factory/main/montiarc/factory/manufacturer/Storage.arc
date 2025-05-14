/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer;

import factory.Factory.*;

component Storage {
  port in Material materialIn,
       in MaterialRequest materialRequest;
  port out Material materialOut,
       out Inventory inventory;

  <<delayed>> compute {}
}