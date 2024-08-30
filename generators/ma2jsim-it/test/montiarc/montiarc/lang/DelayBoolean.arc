/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Boolean;

component DelayBoolean {

  port in Boolean i;
  port <<delayed>> out Boolean o;

  Delay<Boolean> delay;

  i -> delay.i;
  delay.o -> o;

}
