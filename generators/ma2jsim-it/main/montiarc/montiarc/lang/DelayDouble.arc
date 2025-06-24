/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Double;

component DelayDouble {

  port in Double i;
  port <<delayed>> out Double o;

  Delay<Double> delay;

  i -> delay.i;
  delay.o -> o;

}
