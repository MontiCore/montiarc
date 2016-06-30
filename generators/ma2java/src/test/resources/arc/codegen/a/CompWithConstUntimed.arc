package a;

component CompWithConstUntimed {
  timing untimed;
  
  java inv myJavaInv: {
    true == !false;    
  };
  
  ocl inv myOclInv:
    true == !false;
}