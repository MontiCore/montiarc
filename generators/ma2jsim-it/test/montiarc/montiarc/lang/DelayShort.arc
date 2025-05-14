/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Short;

component DelayShort {

  port in Short i;
  port out Short o;

  Delay<Short> delay;

  i -> delay.i;
  delay.o -> o;

}
