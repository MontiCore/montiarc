/* (c) https://github.com/MontiCore/monticore */
package conformance.util.trafo;
import Datatypes.*;
import java.lang.String ;

component Trafo {
  int counter = 0;
  port out int output ;
  port in int input ;

  automaton {
    initial state Anon;

    <<n="0">> Anon -> Anon [input == 1] / {output = 3 ;};
    <<n="1">> Anon -> Anon [input == 2];
    <<n="2">> Anon -> Anon[input == 3] / {};
    <<n="3">> Anon -> Anon [input == 4] / {counter = counter+1;};
  }

}
