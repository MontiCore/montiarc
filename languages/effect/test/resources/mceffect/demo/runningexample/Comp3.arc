/* (c) https://github.com/MontiCore/monticore */
package demo.runningexample;

import Datatypes.*;

component Comp3 {

  port in double in1,
        in double in2;

  port <<delayed>> out double out1,
       <<delayed>> out double out2;

}