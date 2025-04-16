/* (c) https://github.com/MontiCore/monticore */
package elevator;

import java.util.Scanner;

component Buttons {
  port out int pressedOnFloor;

  Scanner s = Scanner.Scanner(System.in);

  automaton {
    initial state S;
    S -> S / {
      System.out.println("---");
      System.out.println("Do you want to call the elvator to a floor ([n]/y)?");
      String res = s.nextLine();
      if (res.contains("y")) {
        System.out.println("Enter a floor:");
        pressedOnFloor = s.nextInt();
      }
    };
  }
}