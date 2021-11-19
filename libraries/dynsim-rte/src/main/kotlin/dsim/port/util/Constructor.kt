/* (c) https://github.com/MontiCore/monticore */
@file:JvmName("Constructor")

package dsim.port.util

import dsim.port.Port

/**
 * A nicer way to construct ports that always sets the type attribute correctly.
 */
inline fun <reified T> port(name: String) = Port(name, T::class.java)
