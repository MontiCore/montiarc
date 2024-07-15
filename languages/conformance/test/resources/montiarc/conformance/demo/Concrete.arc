/* (c) https://github.com/MontiCore/monticore */
package demo;

import Concrete.*;
import java.lang.String;

component Concrete {

  port in Input input,      // See "Concrete.cd" for "Input" Datatype
       in String password;  // Password is transmitted via seperate input port

  port out Output output,   // See "Concrete.cd" for "Output" Datatype
       out int value;       // Value is sent via seperate Output Port

  int counter = 0;          // Global Variables for all States

  automaton {
    initial state Anon;
    state Known;

    Anon -> Known [password == "correct"];

    Known -> Known [input == Input.GET_VALUE] / {
      value = counter;
    };

    Known -> Known [input == Input.INCREASE_VALUE] / {
      counter = counter+1 ;
      value = counter;
    };

    Known -> Anon [input == Input.LOGOUT] / {counter = counter;};

    Anon -> Anon [input == Input.INCREASE_VALUE]/ {
      output = Output.ERROR;
    };

    Anon -> Anon [input == Input.GET_VALUE] /{
      output = Output.ERROR;
    };

    // Comment in the following line to see non-conformance:
    // Anon -> Known [password == "wrong"];
  }
}
