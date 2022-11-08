/* (c) https://github.com/MontiCore/monticore */
package identifiersAreNoJavaKeywords;

/**
 * Invalid model.
 */
component TypeParamHasJavaKeywords <
  //abstract,   // Already leads to a parse error
  assert,
  //boolean,  // Parse error
  //break,    // Parse error
  //byte,     // Parse error
  //case,     // Parse error
  catch,
  //char,     // Parse error
  class,
  //const,    // Parse Error

  continue,
  //default,  // Parse error
  //do,       // Parse error
  //double,   // Parse error
  //else,     // Parse error
  enum,
  //extends,  // Parse error
  //final,    // Parse error
  finally,
  //float,      // Parse error

  //for,        // Parse error
  goto,
  //if,        // Parse error
  implements,
  //import,       // Parse error
  instanceof,
  //int,          // Parse error
  interface,
  //long,         // Parse error
  //native,       // Parse error

  new,
  //package,        // Parse error
  //private,        // Parse error
  //protected,      // Parse error
  //public,         // Parse error
  return,
  //short,          // Parse error
  //static,         // Parse error
  //strictfp,       // Parse error
  super,

  //switch,         // Parse error
  //synchronized,   // Parse error
  this,
  throw,
  throws,
  //transient,      // Parse error
  try
  //void,           // Parse error
  //volatile,       // Parse error
  //while,          // Parse error

> { }