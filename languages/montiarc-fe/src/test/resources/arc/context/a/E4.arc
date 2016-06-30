package a;

component E4 {

  java inv duplicateInvariant: {
    1 == 1;
  };
  
  java inv duplicateInvariant: {
    2 == 2;
  };
  
  java inv uniqueInvariant: {
    3 == 3;
  };
}