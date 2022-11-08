/* (c) https://github.com/MontiCore/monticore */
package identifiersAreNoJavaKeywords;

/**
 * Invalid model.
 */
component FieldHasJavaKeywords {
  //int abstract = 0;   // Already leads to a parse error
  int assert = 0;
  //int boolean = 0;  // Parse error
  //int break = 0;    // Parse error
  //int byte = 0;     // Parse error
  //int case = 0;     // Parse error
  int catch = 0;
  //int char = 0;     // Parse error
  int class = 0;
  //int const = 0;    // Parse Error

  int continue = 0;
  //int default = 0;  // Parse error
  //int do = 0;       // Parse error
  //int double = 0;   // Parse error
  //int else = 0;     // Parse error
  int enum = 0;
  //int extends = 0;  // Parse error
  //int final = 0;    // Parse error
  int finally = 0;
  //int float = 0;      // Parse error

  //int for = 0;        // Parse error
  int goto = 0;
  //int if = 0;        // Parse error
  int implements = 0;
  //int import = 0;       // Parse error
  int instanceof = 0;
  //int int = 0;          // Parse error
  int interface = 0;
  //int long = 0;         // Parse error
  //int native = 0;       // Parse error

  int new = 0;
  //int package = 0;        // Parse error
  //int private = 0;        // Parse error
  //int protected = 0;      // Parse error
  //int public = 0;         // Parse error
  int return = 0;
  //int short = 0;          // Parse error
  //int static = 0;         // Parse error
  //int strictfp = 0;       // Parse error
  int super = 0;

  //int switch = 0;         // Parse error
  //int synchronized = 0;   // Parse error
  int this = 0;
  int throw = 0;
  int throws = 0;
  //int transient = 0;      // Parse error
  int try = 0;
  //int void = 0;           // Parse error
  //int volatile = 0;       // Parse error
  //int while = 0;          // Parse error

}