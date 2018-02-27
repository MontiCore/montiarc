package components.head.parameters;

/*
 * Invalid model.
 * Produces 3 errors in MontiArc3.
 *
 * @implements [Hab16] B1: All names of model elements within a component
 *    namespace have to be unique. (p. 59. Lst. 3.31)
 */
component ConfigurationParametersNotUnique(String wrong, int wrong, String correct,
boolean wrong) {
    // Parameter name "wrong" is ambiguous (three times)
    port 
        in String s1;
}