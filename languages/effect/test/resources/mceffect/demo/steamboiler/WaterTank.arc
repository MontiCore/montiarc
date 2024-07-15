/* (c) https://github.com/MontiCore/monticore */

package demo.steamboiler;

import Datatypes.*;

component WaterTank {
  port in double waterIn,
       in boolean valveStatus;

  port out double waterOut ,
       out double waterLevel;
}