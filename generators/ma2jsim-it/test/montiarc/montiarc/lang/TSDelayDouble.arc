/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Double;

component TSDelayDouble(Double iv) {

  port <<sync>> in Double i;
  port <<sync, delayed>> out Double o;

  TSDelay<Double> delay(iv);

  i -> delay.i;
  delay.o -> o;

}
