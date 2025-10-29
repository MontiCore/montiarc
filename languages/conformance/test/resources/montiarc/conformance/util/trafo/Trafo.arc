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

    <<n="0">> Anon -> Anon [input == 1] input / {output = 3 ;}
    <<n="1">> Anon -> Anon [input == 2] input ;
    <<n="2">> Anon -> Anon [input == 3] input / {}
    <<n="3">> Anon -> Anon [input == 4] input / {counter = counter+1;}
  }

}
