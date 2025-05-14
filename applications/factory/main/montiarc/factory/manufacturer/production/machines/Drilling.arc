/* (c) https://github.com/MontiCore/monticore */
package factory.manufacturer.production.machines;

import factory.Factory.*;

component Drilling<PIType extends WorkPiece, POType extends WorkPiece> {
  port in PIType pieceIn,
       in ProductConfig config;
  port out POType pieceOut;

  <<delayed>> compute {}
}