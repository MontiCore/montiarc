package a;

component CG12 {
  component CG12true(5) correct;
  
  component CG12true wrong1;
  
  component CG12false(5) wrong2;
  
  component CG12false wrong3;
  
  component CG12false2("asdf","4") wrong4;

}