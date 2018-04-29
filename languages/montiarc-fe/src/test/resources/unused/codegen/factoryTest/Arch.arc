package factoryTest;
import componentTest.Merge;

component Arch {
  
  port 
    in String str,
    in Integer,
    out Boolean bool;
    
  component Basic b1;
  <<T="java.lang.String", encoding="ISO-8859-1">> component Merge;
}