package basic;

component Line {
  
  port
    in Integer inp,
    out Integer outp;
    
    connect inp -> outp;
}