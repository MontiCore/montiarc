package a;

/*
 * Used in ignored test in MontiArc 3.
 * References (old?) tickets 116 and #36
 *
 * Threw an error for invalid configuration arguments.
 */
component UseCompWithCfgArg {
        
    component CompWithCfgArgPerson(new Person()) p1;
    component CompWithCfgArgPerson(new Student()) p2;
    
    component CompWithCfgArgStudent(new Person()) p3;
    component CompWithCfgArgStudent(new Student()) p4;

}