/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Float;

component TSDelayFloat(Float iv) {

  port <<sync>> in Float i;
  port <<sync, delayed>> out Float o;

  TSDelay<Float> delay(iv);

  i -> delay.i;
  delay.o -> o;

}
