package components.body;

/*
 * Invalid model.
 *
 * @implements [Wor16] AU3: The names of all inputs, outputs, and variables
 *    are unique. (p. 98. Lst. 5.10)
 * @implements [Hab16] B1: All names of model elements within a component
 *    namespace have to be unique. (p. 59. Lst. 3.31)
 */
component AmbiguousIdentifiers {
    port out String x; // Error: The identifier 'x' is ambiguous

    component B x { //Error: The identifier 'x' is ambiguous
      port out String x;
    }

    component B x; // Error: The identifier 'x' is ambiguous

    connect x.x -> x;
}