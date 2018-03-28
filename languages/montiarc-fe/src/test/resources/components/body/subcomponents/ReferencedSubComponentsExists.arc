package components.body.subcomponents;

/**
 * Valid model. 
 * @implements [Hab12] R3: Full qualified subcomponent types exist in the named package. (p. 28)
 * @implements [Hab12] R4: Unqualified subcomponent types either exist in the current package 
 * or are imported using an import statement. (p. 28) 
 */
component ReferencedSubComponentsExists {
  port 
    in String s1,
    out String sout1;

  component Buffer("buffer");
  
  connect s1 -> buffer.input;
  connect buffer.output -> sout1;
}