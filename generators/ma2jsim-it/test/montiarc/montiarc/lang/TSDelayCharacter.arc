/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang;

import java.lang.Character;

component TSDelayCharacter(Character iv) {

  port <<sync>> in Character i;
  port <<sync, delayed>> out Character o;

  TSDelay<Character> delay(iv);

  i -> delay.i;
  delay.o -> o;

}
