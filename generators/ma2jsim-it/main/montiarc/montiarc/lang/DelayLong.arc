/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Long;

component DelayLong {

  port in Long i;
  port <<delayed>> out Long o;

  Delay<Long> delay;

  i -> delay.i;
  delay.o -> o;

}
