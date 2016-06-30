package a;

component E3 {
  
  component x.R5Partner p1;
  component x.R5Partner p2;
  
  port 
    in String defIn,
    out Integer defOut;
  
  connect portIn -> p1.portIn;
  connect p1.portOut -> portOut;
  
  connect defIn -> p2.portIn;
  connect p1.portOut -> defOut;
}