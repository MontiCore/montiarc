/* (c) https://github.com/MontiCore/monticore */
package identifiersAreNoJavaKeywords;

/**
 * Invalid model.
 */
component ComponentInstanceHasJavaKeywords {
  component Inner {}

  //Inner abstract;   // Already leads to a parse error
  Inner assert;
  //Inner boolean;  // Parse error
  //Inner break;    // Parse error
  //Inner byte;     // Parse error
  //Inner case;     // Parse error
  Inner catch;
  //Inner char;     // Parse error
  Inner class;
  //Inner const;    // Parse Error

  Inner continue;
  //Inner default;  // Parse error
  //Inner do;       // Parse error
  //Inner double;   // Parse error
  //Inner else;     // Parse error
  Inner enum;
  //Inner extends;  // Parse error
  //Inner final;    // Parse error
  Inner finally;
  //Inner float;      // Parse error

  //Inner for;        // Parse error
  Inner goto;
  //Inner if;        // Parse error
  Inner implements;
  //Inner import;       // Parse error
  Inner instanceof;
  //Inner Inner;          // Parse error
  Inner interface;
  //Inner long;         // Parse error
  //Inner native;       // Parse error

  Inner new;
  //Inner package;        // Parse error
  //Inner private;        // Parse error
  //Inner protected;      // Parse error
  //Inner public;         // Parse error
  Inner return;
  //Inner short;          // Parse error
  //Inner static;         // Parse error
  //Inner strictfp;       // Parse error
  Inner super;

  //Inner switch;         // Parse error
  //Inner synchronized;   // Parse error
  Inner this;
  Inner throw;
  Inner throws;
  //Inner transient;      // Parse error
  Inner try;
  //Inner void;           // Parse error
  //Inner volatile;       // Parse error
  //Inner while;          // Parse error

  Inner exports;
  Inner module;
  //Inner non-sealed;     // Parse error
  Inner open;
  Inner opens;
  Inner permits;
  Inner provides;
  Inner record;

  component Inner1 requires {}
  component Inner2 sealed, to, transitive {}
  Inner uses;
  Inner var;
  Inner with;
  Inner yield;
}