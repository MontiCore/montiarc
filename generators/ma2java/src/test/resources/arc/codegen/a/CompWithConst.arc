package a;

component CompWithConst {
  
  java inv myJavaInv: {
    true == !false;    
  };
  
  ocl inv myOclInv:
    true == !false;
}