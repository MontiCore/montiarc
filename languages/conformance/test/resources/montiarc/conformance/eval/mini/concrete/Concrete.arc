/* (c) https://github.com/MontiCore/monticore */
package conformance.eval.mini.concrete;
import Datatypes.*;
import java.lang.String;

component Concrete {

  port in Input input;
  port out Output output;

  int counter = 0;


  automaton {

    initial {} state Anon;
    state Known;

    Anon -> Known [input == Input.PASSWORD];

    Known -> Known [input == Input.INCREASE_VALUE] / {
      counter = counter + 1 ;
      output = Output.DONE;
    };

    Known -> Anon [input == Input.LOGOUT];

    Anon -> Anon [input == Input.INCREASE_VALUE]/ {
      output = Output.ERROR;
    };

    Anon -> Anon [input == Input.INCREASE_VALUE]/ {
      output = Output.ERROR;
    };
  }
}
