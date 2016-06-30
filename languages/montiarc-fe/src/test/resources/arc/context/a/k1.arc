package a;

import java.lang.String;

component k1 {
  
  port
    in String BigName,
    out String smallName,
    out String out2;
  
  
  component x.EmptyReference BigRef, smallRef;
  
  connect bigName -> bigRef.pIn, smallRef.pIn;
  connect bigRef.pOut -> smallName;
  connect smallRef.pOut -> out2;
  
  java inv BigLetterInvariant: {
    1 == 1;
  };
    
  java inv smallLetterInvariant: {
    1 == 1;
  };
}