/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import montiarc.generator.helper.ComponentHelper;

/**
 * Prints member and getter for component's subcomponents.
 */

public class Subcomponents {
  public String print(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder();
    for(ComponentInstanceSymbol subcomponent: comp.getSubComponents()){
      String type = ComponentHelper.getSubComponentTypeName(subcomponent);
      builder.newLineIfNotEmpty()
        .append("protected ")
        .append(type)
        .append(" ")
        .append(subcomponent.getName())
        .append(";")
        .newLineIfNotEmpty()
        .newLine()
        .append("public ")
        .append(type)
        .append(" getComponent")
        .appendWithFirstCharUpper(subcomponent.getName())
        .append("() {")
        .newLineIfNotEmpty()
        .append("  ")
        .append("return this.")
        .append(subcomponent.getName(), "  ")
        .append(";")
        .newLineIfNotEmpty()
        .append("}")
        .newLine();
    }
    return builder.toString();
  }
}