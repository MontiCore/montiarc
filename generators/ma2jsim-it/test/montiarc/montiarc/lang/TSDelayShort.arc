/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Short;

component TSDelayShort(Short iv) {

  port <<sync>> in Short i;
  port <<sync, delayed>> out Short o;

  TSDelay<Short> delay(iv);

  i -> delay.i;
  delay.o -> o;

}
