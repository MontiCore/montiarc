package components;

/*
 * Valid model.
 *
 * Resolving 'sIn' and 'foo' should result in portsymbols of type 'java.lang.Boolean'
 * as the subcomponent 'foo' of the parent component is hidden, as well as
 * the port 'sIn' of type 'java.lang.String'.
 */
component NameSpaceHiding extends NameSpaceHidingSuper{

    port
        in Boolean sIn,
        in Boolean foo;
}
