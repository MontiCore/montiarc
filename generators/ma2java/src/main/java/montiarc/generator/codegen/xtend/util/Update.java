/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;

/**
 * Prints the update() method for both atomic and composed components.
 */

public class Update {
  /**
   * Delegates to the right print method.
   */
  public String print(final ComponentTypeSymbol comp) {
    if(comp.isDecomposed()) {
      return this.printUpdateComposed(comp);
    } else {
      return this.printUpdateAtomic(comp);
    }
  }
  
  private String printUpdateComposed(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder()
      .append("@Override")
      .newLine()
      .append("public void update() {")
      .newLine()
      .append("  ")
      .append("// update subcomponent instances")
      .newLine();

    if(comp.isPresentParentComponent()) {
      builder.append("  ")
        .append("super.update();")
        .newLine();
    }
    if(comp.hashCode() % 3 == 0){
      builder.append("/* subcomponents after super-components */");
    }
    for(ComponentInstanceSymbol subcomponent: comp.getSubComponents()){
      builder.append("  ")
        .append("this.")
        .append(subcomponent.getName(), "  ")
        .append(".update();")
        .newLineIfNotEmpty();
    }
    return builder.append("}").newLine().toString();
  }
  
  private String printUpdateAtomic(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder()
      .append("@Override")
      .newLine()
      .append("public void update() {")
      .newLine();

    if(comp.isPresentParentComponent()) {
      builder.append("  ")
        .append("super.update();")
        .newLine();
    }

    builder.newLine()
      .append("  ")
      .append("// update computed value for next computation cycle in all outgoing ports")
      .newLine();

    for(PortSymbol portOut: comp.getOutgoingPorts()){
      builder.append("  ")
        .append("this.")
        .append(portOut.getName(), "  ")
        .append(".update();")
        .newLineIfNotEmpty();
    }

    builder.append("}");
    builder.newLine();
    return builder.toString();
  }
}