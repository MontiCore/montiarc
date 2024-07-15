/* (c) https://github.com/MontiCore/monticore */
package conformance.util.cocos;
import CoCos.*;

component CoCos {

  int counter = 0;

  automaton {
    initial state Anon;

    <<n="0">> Anon -> Anon [counter == 0] / {
    counter = 10;
    counter = 11;
    counter++;};
  }
}
