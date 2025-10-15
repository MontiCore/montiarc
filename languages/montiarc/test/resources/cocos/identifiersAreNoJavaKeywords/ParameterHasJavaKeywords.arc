/* (c) https://github.com/MontiCore/monticore */
package identifiersAreNoJavaKeywords;

/**
 * Invalid model.
 */
component ParameterHasJavaKeywords (
    //int abstract,   // Already leads to a parse error
    int assert,
    //int boolean,  // Parse error
    //int break,    // Parse error
    //int byte,     // Parse error
    //int case,     // Parse error
    int catch,
    //int char,     // Parse error
    int class,
    //int const,    // Parse Error

    int continue,
    //int default,  // Parse error
    //int do,       // Parse error
    //int double,   // Parse error
    //int else,     // Parse error
    int enum,
    //int extends,  // Parse error
    //int final,    // Parse error
    int finally,
    //int float,      // Parse error

    //int for,        // Parse error
    int goto,
    //int if,        // Parse error
    int implements,
    //int import,       // Parse error
    int instanceof,
    //int int,          // Parse error
    int interface,
    //int long,         // Parse error
    //int native,       // Parse error

    int new,
    //int package,        // Parse error
    //int private,        // Parse error
    //int protected,      // Parse error
    //int public,         // Parse error
    int return,
    //int short,          // Parse error
    //int static,         // Parse error
    //int strictfp,       // Parse error
    int super,

    //int switch,         // Parse error
    //int synchronized,   // Parse error
    int this,
    int throw,
    int throws,
    //int transient,      // Parse error
    int try
    //int void,           // Parse error
    //int volatile,       // Parse error
    //int while,          // Parse error

) { }
