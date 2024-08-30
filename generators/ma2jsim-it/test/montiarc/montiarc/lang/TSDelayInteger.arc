/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Integer;

component TSDelayInteger(Integer iv) {

  port <<sync>> in Integer i;
  port <<sync, delayed>> out Integer o;

  TSDelay<Integer> delay(iv);

  i -> delay.i;
  delay.o -> o;

}
