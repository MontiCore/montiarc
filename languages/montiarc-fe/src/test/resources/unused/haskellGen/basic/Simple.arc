package basic;

component Simple {
  
  port
    in Integer in1,
    in Integer in2,
    out Integer outp;
    
  component Add;
  component Mult;
  component Delay;
  
  connect delay.outp -> add.num1;
  connect in1 -> add.num2;
  connect add.sum -> mult.num1;
  connect in2 -> mult.num2;
  connect mult.outp -> delay.inp;
  connect mult.mul -> outp;
}