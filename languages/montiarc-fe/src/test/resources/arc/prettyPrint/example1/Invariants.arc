package example1;

component Invariants {

    java inv test2: {
        a == b;
    };

    ocl inv test1:
        a == b;
  
    ocl inv test3: 
        b == c;
        

}