/* (c) https://github.com/MontiCore/monticore */
package testing.statements;

import java.util.Arrays;

/**
 * model for testing the generation of some loops
 */
component Loops {

  port in int number;
  port out String t;

  compute {
    String text = "";
    if (number == 0){
      for(char letter: Arrays.asList('a', 'b', 'c', 'd')){
        text += letter;
      }
    } else if(number > 0) {
      for(int i = 2; i < number; i++){
        text += i;
      }
    } else {
      int x = 0;
      while (x > number){
        text += -x;
        x -= 2;
      }
    }
    t = text;
  }
}
