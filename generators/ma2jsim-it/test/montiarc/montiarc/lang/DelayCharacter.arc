/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Character;

component DelayCharacter {

  port in Character i;
  port <<delayed>> out Character o;

  Delay<Character> delay;

  i -> delay.i;
  delay.o -> o;

}
