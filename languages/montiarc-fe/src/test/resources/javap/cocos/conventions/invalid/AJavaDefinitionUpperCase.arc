package javap.cocos.conventions.invalid;


component AJavaDefinitionUpperCase {
  
  port
    in String sIn;
  
  compute printSIn {
    System.out.println(sIn);
    int bubu = 0;
    sIn = bubu;
  }
}