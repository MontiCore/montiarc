/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Boolean;

component TSDelayBoolean(Boolean iv) {

  port <<sync>> in Boolean i;
  port <<sync, delayed>> out Boolean o;

  TSDelay<Boolean> delay(iv);

  i -> delay.i;
  delay.o -> o;

}
