/* (c) https://github.com/MontiCore/monticore */
package demo.runningexample;

import Datatypes.*;

component Comp2 {

  port in double in1,
       in double in2;

  port out double out1,
       out double out2,
       out double out3;

  <<delayed>> compute {}
}
