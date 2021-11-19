/* (c) https://github.com/MontiCore/monticore */
package dsim.conf

import dsim.comp.IReconfigurable

/**
 * Functional representation of reconfiguration actions.
 * Constrained to the IReconfigurable Interface to allow only specific set of actions.
 * Makes use of the Kotlin DSL features.
 */
typealias ChangeScript = IReconfigurable.() -> Unit
