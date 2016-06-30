package a;

component CG4 {
  
  component y.R6GenericPartner pWrong1;
  
  component y.R6GenericPartner pWrong2;
  
  component y.R6GenericPartner<T> pWrong3;
  
  component y.R6GenericPartner<java.lang.String> pCorrect;

}