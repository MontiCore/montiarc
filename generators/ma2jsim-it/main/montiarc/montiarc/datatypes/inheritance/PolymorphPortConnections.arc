/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.inheritance;

import montiarc.types.Person;
import montiarc.types.Student;

component PolymorphPortConnections {

  WithStudentOut studentComp;
  WithPersonIn personComp;

  studentComp.student -> personComp.person;
}
