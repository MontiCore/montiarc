/* (c) https://github.com/MontiCore/monticore */
package conformance.automaton2smt.concrete;
import Datatypes.*;
import java.lang.String;

component Concrete {

  port in Input input,
       in String password;

  port out Output output,
       out int value;

  int counter = 0;

  automaton {
    initial state Anon;
    state Known;

    <<n="0">> Anon -> Known [password == "correct"] password / {
      value = 10;
      output = Output.ERROR;
    }

    <<n="1">> Known -> Known [input == Input.GET_VALUE] input / {
      value = counter;
    }

    <<n="2">> Known -> Known [input == Input.INCREASE_VALUE] input / {
      counter = counter + 1;
      value = counter;
    }

    <<n="3">> Known -> Anon [input == Input.LOGOUT] input;
    <<n="4">> Anon -> Anon [input == Input.LOGOUT] input;

    <<n ="5">> Anon -> Anon [input == Input.INCREASE_VALUE] input / {
      output = Output.ERROR;
    }

    <<n="6">> Anon -> Anon [input == Input.GET_VALUE] input / {
      output = Output.ERROR;
    }
  }
}
