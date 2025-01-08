/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer.production.machines;

import factory.Factory.*;

component Grinding<PIType extends WorkPiece, POType extends WorkPiece> {
  port in PIType pieceIn,
       in ProductConfig config;
  port <<delayed>> out POType pieceOut;
}