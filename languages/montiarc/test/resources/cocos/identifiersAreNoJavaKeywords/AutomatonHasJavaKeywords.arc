/* (c) https://github.com/MontiCore/monticore */
package identifiersAreNoJavaKeywords;

/**
 * Invalid model.
 */
component AutomatonHasJavaKeywords {
  automaton {
    //state abstract;   // Already leads to a parse error
    initial state assert;
    //state boolean;  // Parse error
    //state break;    // Parse error
    //state byte;     // Parse error
    //state case;     // Parse error
    state catch;
    //state char;     // Parse error
    state class;
    //state const;    // Parse Error

    state continue;
    //state default;  // Parse error
    //state do;       // Parse error
    //state double;   // Parse error
    //state else;     // Parse error
    state enum;
    //state extends;  // Parse error
    //state final;    // Parse error
    state finally;
    //state float;      // Parse error

    //state for;        // Parse error
    state goto;
    //state if;        // Parse error
    state implements;
    //state import;       // Parse error
    state instanceof;
    //state state;          // Parse error
    state interface;
    //state long;         // Parse error
    //state native;       // Parse error

    state new;
    //state package;        // Parse error
    //state private;        // Parse error
    //state protected;      // Parse error
    //state public;         // Parse error
    state return;
    //state short;          // Parse error
    //state static;         // Parse error
    //state strictfp;       // Parse error
    state super;

    //state switch;         // Parse error
    //state synchronized;   // Parse error
    state this;
    state throw;
    state throws;
    //state transient;      // Parse error
    state try;
    //state void;           // Parse error
    //state volatile;       // Parse error
    //state while;          // Parse error

    state exports;
    state module;
    //state non-sealed;     // Parse error
    state open;
    state opens;
    state permits;
    state provides;
    state record;

    state requires;
    state sealed;
    state to;
    state transitive;
    state uses;
    state var;
    state with;
    state yield;
  }
}
