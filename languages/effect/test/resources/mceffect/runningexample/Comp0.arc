/* (c) https://github.com/MontiCore/monticore */
package demo.runningexample;

import Datatypes.*;

component Comp0 {

  port in double in1;

  port out double out1,
       out double out2;

  Comp1 comp1;
  Comp2 comp2;
  Comp3 comp3;

  in1 -> comp1.in1;

  comp1.out1 -> comp2.in1;
  comp1.out2 -> comp3.in1;

  comp3.out1 -> comp1.in2;
  comp3.out2 -> comp2.in2;

  comp2.out1 -> out1;
  comp2.out2 -> out2;
  comp2.out3 -> comp3.in2;
}