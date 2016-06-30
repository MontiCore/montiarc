package a;
import java.lang.Boolean;

component R5 {
  
  port
    in String portIn,
    out Boolean somePort,
    out String portOut;
  
  component x.R5Partner p1;
  component x.R5Partner p2;
  component x.R5PartnerTwo p3;
  
  // correct
  connect portIn -> p1.portIn, p2.portIn;
  connect p1.portOut -> p3.Integer;
  connect p3.Boolean -> p2.Boolean;
  
  
  // port does not exist
  connect p1.notExists -> p3.inNotExists;
  connect p3.outNotExists -> somePort;

}