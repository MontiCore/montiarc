/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Byte;

component TSDelayByte(Byte iv) {

  port sync in Byte i;
  port <<delayed>> sync out Byte o;

  TSDelay<Byte> delay(iv);

  i -> delay.i;
  delay.o -> o;

}
