/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.inheritance;

import montiarc.types.Student;

component WithStudentOut {
  port sync out Student student;

  automaton {
    initial state S;
    S -> S / {
      student = Student.Student();
    }
  }
}
