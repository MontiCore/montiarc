/* (c) https://github.com/MontiCore/monticore */
package conformance.automaton2smt.concrete;
import Datatypes.*;
import java.lang.String ;

component WrongConcrete {

  port in Input input,
       in String password;

  port out Output output,
       out int value;

  int counter = 0;

  automaton {
    initial {} state Anon;
    state Known;

    <<n="0">> Anon -> Known [password == "correct"] / {counter = counter;};

    <<n="1">> Known -> Known [input == Input.GET_VALUE] / {
      value = counter;
      counter = counter;
    };

    <<n="2">> Known -> Known [input == Input.INCREASE_VALUE] / {
      counter = counter +1;
      value = counter;
    };

    <<n="3">> Known -> Anon [input == Input.LOGOUT] / {counter = counter;};
    <<n="4">> Anon -> Anon [input == Input.LOGOUT] / {counter = counter;};

    <<n ="5">> Anon -> Anon [input == Input.INCREASE_VALUE] / {
      output = Output.ERROR;
      counter = counter;
    };

    <<n="6">> Anon -> Anon [input == Input.GET_VALUE] / {
      output = Output.ERROR;
      counter = counter;
    };

    Anon -> Known [password == "wrong"] / {counter = counter;};
  }
}
