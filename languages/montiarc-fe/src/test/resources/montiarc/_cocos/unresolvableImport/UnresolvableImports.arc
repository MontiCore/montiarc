/* (c) https://github.com/MontiCore/monticore */
package unresolvableImport;

/* The following three lines should throw errors because of the non existing package 'missing'. However we currently do
 * not check for missing packages.
  import exists.missing.*;
  import exists.missing.DoNotPrintMe;  // An error for missing package 'exists.missing' should be logged, but not for 'DoNotPrintMe' (obviously can't exist, because its package is missing).
  import missing.*;
*/

import exists.Missing;
import exists.PresentOOType.missingStaticField;
import exists.PresentOOType.missingStaticMethod;

import exists.TypeVar;       // Erroneous, as we do not import type variables.
import exists.Port;          // Erroneous, as we do not import ports.
import exists.CompInstance;  // Erroneous, as we do not import component instances.
import exists.ArcFeature;    // Erroneous, as we do not import arc features.
import exists.SomeState;     // Erroneous, as we do not import automaton states.

import exists.PresentOOType.instanceField;  // Erroneous, as we only allow importing *static* fields
import exists.PresentOOType.instanceMethod; // Erroneous, as we only allow importing *static* methods

/**
 * Invalid. For tests:
 *   * let the package 'exists' exist,
 *   * but not its subpackage 'missing', nor the type 'Missing' in it.
 *   * .TypeVar, .Port, .CompInstance, .ArcFeature, and .SomeState should exist having the corresponding symbol kind.
 *   * let PresentOOType have 'instanceField' and 'instanceMethod' (both non-static),
 *   * but not 'missingStaticField', nor 'missingStaticMethod'.
 */
component UnresolvableImports { }