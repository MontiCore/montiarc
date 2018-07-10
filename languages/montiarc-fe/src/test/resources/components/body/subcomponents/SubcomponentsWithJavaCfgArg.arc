package components.body.subcomponents;

import components.body.subcomponents._subcomponents.ComponentWithJavaCfgArgPerson;
import components.body.subcomponents._subcomponents.ComponentWithJavaCfgArgStudent;
import types.Person;
import types.Student;

/**
* Valid model.
*/
component SubcomponentsWithJavaCfgArg {
        
    component ComponentWithJavaCfgArgPerson(new Person()) p1;
    component ComponentWithJavaCfgArgPerson(new Student()) p2;  
    
    //component ComponentWithJavaCfgArgStudent(new Person()) p3; //invalid downcast Person -> Student
    component ComponentWithJavaCfgArgStudent(new Student()) p4;

}