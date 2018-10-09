package components.body.ajava;

/*
 * Invalid model.
 *
 * @implements No literature reference, AJava CoCo
 */
component JavaPVariableIdentifiersUnique {


  port
    in String s;
    
  Integer i;

  compute {
    String s = "Hello";
    Integer i;
    int w = 2;
    boolean w = false;
  }

}