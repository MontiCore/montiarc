package a;

/*
 * TODO: Invalid model?
 * Produces 2 errors and 1 warning in MontiArc3.
 * Warning: "ArcdErrorCodes.NamingConventionsInjured"
 *
 * @implements TODO
 */
component E1_2 {
    port
        in Object,
        in Throwable,
        out Throwable throwable,
        in Integer,
        out Integer someIntName,
        in String string,
        out String sOut;
}