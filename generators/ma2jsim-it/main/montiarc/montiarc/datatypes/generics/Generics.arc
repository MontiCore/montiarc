/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import montiarc.types.Person;
import montiarc.types.Student;
import java.util.List;

component Generics<T, U extends Person>(T parameter, List<U> listParameter, List<Person> boundListParameter) {
  port in T inPort;
  port in List<T> listInPort;
  port in List<Person> boundListInPort;

  T field = parameter;
  List<U> listField = listParameter;
  List<Person> boundListField = boundListParameter;


  <<sync>> automaton {
    initial state S;
    S -> S / {
      T variable = field;
      List<U> listVariable = listField;
      List<Person> boundListVariable = boundListField;
    };
  }
}
