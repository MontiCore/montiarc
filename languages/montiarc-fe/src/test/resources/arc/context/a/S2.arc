package a;

import java.lang.String;

component S2 {
  
  port 
    in connect;
  
  port in String out;
    
  component out;
  
  component x.EmptyReference in;
  
  <<connect = "asd">> component x.EmptyReference correct;

}