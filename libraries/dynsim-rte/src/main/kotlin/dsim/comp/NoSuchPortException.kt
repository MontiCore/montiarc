/* (c) https://github.com/MontiCore/monticore */
package dsim.comp

import java.lang.Exception

class NoSuchPortException(portName: String = "") : Exception(portName)
