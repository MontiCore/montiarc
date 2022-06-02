/* (c) https://github.com/MontiCore/monticore */
package unresolvableImport;

import exists.present.*;
import exists.PresentType;
import exists.PresentOOType;
import exists.ComponentType;
import exists.PresentOOType.staticField;
import exists.PresentOOType.staticMethod;

/**
 * Valid, if packages 'exists' and 'exists.present' exist, in 'exists.present' there is any component type / type /
 * ootype. Moreover, the given other imports should also be resolvable. 'staticField' and 'staticMethod' should
 * (obviously) be static.
 */
component ResolvableImports { }