/* (c) https://github.com/MontiCore/monticore */
package mceffect.steamboiler;

import Datatypes.*;

component Heater {

  port in double heat,
       in double waterIn,
       out double waterOut;
}