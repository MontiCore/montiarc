package components.head.inheritance;

/*
 * Invalid model.
 * The component extends the component 'components.head.inheritance
 * .HasRequiredAndOptionalConfigParameters' but specifies too few configuration
 * parameters. More specifically, there is no parameter of type
 * 'java.lang.String' in the second position and no parameter of 
 *
 * @implements R14: Components that inherit from a parametrized component
 *      provide configuration parameters with the same types, but are
 *      allowed to provide more parameters. (p.69 Lst. 3.49)
 * @author Michael Mutert
 * TODO Add test
 */
component TooFewConfigurationParameters (Integer intParam)
    extends HasRequiredAndOptionalConfigParameters
{
  // Empty body
}