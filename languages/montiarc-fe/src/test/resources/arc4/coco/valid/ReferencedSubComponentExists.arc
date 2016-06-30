package valid;

component ReferencedSubComponentExists {
  port out String sout1,
       out String sout2;

  component CorrectCompInValid;
    
  component common.CorrectCompInCommon ccib;
  
  connect s1 -> correctCompInValid.stringIn, ccib.stringIn;
  connect correctCompInValid.stringOut -> sout1;
  connect ccib.stringOut -> sout2;
}