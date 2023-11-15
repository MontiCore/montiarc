/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.inheritance;

import montiarc.types.Student;

component WithStudentOut {
  port out Student student;

  <<sync>> automaton {
    initial state S;
    S -> S / { };
  }
}
