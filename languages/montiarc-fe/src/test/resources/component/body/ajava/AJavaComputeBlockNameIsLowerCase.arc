package component.body.ajava;

/**
 * Invalid model. If AJava compute blocks have names, these must be
 * upper case.
 */
component AJavaComputeBlockNameIsLowerCase {
  
  port
    in String sIn;
  
  compute printSIn {
    System.out.println(sIn);
  }
}
