/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Integer;

component DelayInteger {

  port in Integer i;
  port <<delayed>> out Integer o;

  Delay<Integer> delay;

  i -> delay.i;
  delay.o -> o;

}
