package a;

import java.lang.String;
import java.lang.Integer;
import a.myTypes.List;
import a.myTypes.NewType;
import z.GenericPartner;

component TypesTest {
  
  port 
    in NewType<String, Integer> nt;
  
  port 
    in String incoming,
    out Integer outgoing,
    out String strOut;
    
  port
    in List<NewType<String, List<String>>> complexIn,
    out List<NewType<String, List<String>>> complexOut;
  
  component GenericComp<String, Integer> gen;
  component GenericPartner<List<NewType<String, List<String>>>> complexPartner;
  
  connect incoming -> gen.incoming;
  connect nt -> gen.nt;
  connect gen.outgoing -> outgoing;
  connect gen.outK -> strOut;
  
  connect complexIn -> complexPartner.tIn;
  connect complexPartner.tOut -> complexOut;
}