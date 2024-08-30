/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Long;

component TSDelayLong(Long iv) {

  port <<sync>> in Long i;
  port <<sync, delayed>> out Long o;

  TSDelay<Long> delay(iv);

  i -> delay.i;
  delay.o -> o;

}
