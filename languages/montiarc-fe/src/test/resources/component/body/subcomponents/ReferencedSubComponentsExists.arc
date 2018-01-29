package component.body.subcomponents;

component ReferencedSubComponentsExists {
  port 
    in String s1,
    out String sout1;

  component Buffer;
  
  connect s1 -> buffer.input;
  connect buffer.output -> sout1;
}