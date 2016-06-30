package a;

import a.myTypes.NewType;
import z.GenericPartner;

component GenericComp<K, V> {

  port 
    in K incoming,
    in NewType<K, V> nt,
    out V outgoing,
    out K outK;
    
  component GenericPartner<K> gp;
  
  connect incoming -> gp.tIn;
  connect gp.tOut -> outK;
}