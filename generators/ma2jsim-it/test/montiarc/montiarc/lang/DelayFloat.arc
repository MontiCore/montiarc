/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Float;

component DelayFloat {

  port in Float i;
  port out Float o;

  Delay<Float> delay;

  i -> delay.i;
  delay.o -> o;

}
