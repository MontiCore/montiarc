/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util;

import arcbasis._symboltable.PortSymbol;
import montiarc.generator.helper.ComponentHelper;

import java.util.Collection;

/**
 * Prints the attributes and getter and setter for ports.
 */

public class Ports {
  public String print(final Collection<PortSymbol> ports) {
    ConcatenationBuilder builder = new ConcatenationBuilder();
    for(final PortSymbol port : ports) {
      String type = ComponentHelper.getRealPortTypeString(port.getComponent().get(), port);
      String name = port.getName();
      builder.newLineIfNotEmpty()
        .newLineIfNotEmpty()
        .newLine()
        .append("protected Port<")
        .append(type)
        .append("> ")
        .append(name)
        .append(";")
        .newLineIfNotEmpty()
        .newLine()
        .append("public Port</*? extends */")
        .append(type)
        .append("> getPort")
        .appendWithFirstCharUpper((name))
        .append("() {")
        .newLineIfNotEmpty()
        .append("      ")
        .append("return this.")
        .append(name, "      ")
        .append(";")
        .newLineIfNotEmpty()
        .append("}")
        .newLine()
        .newLine()
        .append("public void setPort")
        .appendWithFirstCharUpper((name))
        .append("(Port<")
        .append(type)
        .append("> ")
        .append(name)
        .append(") {")
        .newLineIfNotEmpty()
        .append("      ")
        .append("this.")
        .append(name, "      ")
        .append(" = ")
        .append(name, "      ")
        .append(";")
        .newLineIfNotEmpty()
        .append("}")
        .newLine()
        .newLine();
    }
    return builder.toString();
  }
}