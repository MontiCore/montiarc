/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Byte;

component DelayByte {

  port in Byte i;
  port <<delayed>> out Byte o;

  Delay<Byte> delay;

  i -> delay.i;
  delay.o -> o;

}
