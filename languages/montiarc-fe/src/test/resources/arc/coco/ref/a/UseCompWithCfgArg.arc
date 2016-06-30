package a;

component UseCompWithCfgArg {
        
    component CompWithCfgArgPerson(new Person()) p1;
    component CompWithCfgArgPerson(new Student()) p2;
    
    component CompWithCfgArgStudent(new Person()) p3;
    component CompWithCfgArgStudent(new Student()) p4;

}