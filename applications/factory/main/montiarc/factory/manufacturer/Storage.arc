/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer;

import factory.Factory.*;

component Storage {
  port in Material materialIn,
       in MaterialRequest materialRequest;
  port <<delayed>> out Material materialOut,
       out Inventory inventory;

}