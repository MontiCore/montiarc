package basic;

component DoubleDelay {
  
  port
    in T inp1,
    in T inp2,
    out T outp1,
    out T outp2;
    
    component Delay d1, d2;
    
    connect inp1 -> d1.inp;
    connect inp2 -> d2.inp;
    connect d1.outp -> outp1;
    connect d2.outp -> outp2;
}