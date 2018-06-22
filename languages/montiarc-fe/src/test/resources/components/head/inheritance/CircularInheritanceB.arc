package components.head.inheritance;

/**
 * @implements [Hab16] R11: Inheritance cycles of component types are forbidden. (p. 67, lst. 3.46)
 * Invalid model. Circular inheritance is not allowed (see CircularInheritanceA.arc)
 */
component CircularInheritanceB extends CircularInheritanceA {}
