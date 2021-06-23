/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import montiarc.generator.helper.ComponentHelper;

/**
 * Prints the setup for both atomic and composed components.
 */

public class Setup {
  /**
   * Delegates to the right print method.
   */
  public String print(final ComponentTypeSymbol comp) {
    if(comp.isAtomic()) {
      return this.printSetupAtomic(comp);
    } else {
      return this.printSetupComposed(comp);
    }
  }
  
  protected String printSetupAtomic(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder();
    builder.append("@Override");
    builder.newLine();
    builder.append("public void setUp() {");
    builder.newLine();
    {
      if(comp.isPresentParentComponent()) {
        builder.append("super.setUp();");
        builder.newLine();
      }
    }
    builder.newLine()
      .newLine()
      .append("// set up output ports")
      .newLine();
    {
      for(PortSymbol portOut: comp.getOutgoingPorts()){
        builder.append("this.")
          .append(portOut.getName())
          .append(" = new Port<")
          .append(">();")
          .newLineIfNotEmpty();
      }
    }
    builder.newLine();
    builder.append("this.initialize();");
    builder.newLine();
    builder.newLine();
    builder.append("}");
    builder.newLine();
    return builder.toString();
  }
  
  protected String printSetupComposed(final ComponentTypeSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    ConcatenationBuilder builder = new ConcatenationBuilder();
    builder.append("    ");
    builder.append("@Override");
    builder.newLine();
    builder.append("    ");
    builder.append("public void setUp() {");
    builder.newLine();

    if(comp.isPresentParentComponent()) {
      builder.append("    ");
      builder.append("super.setUp();");
      builder.newLine();
    }

    builder.append("    ");
    builder.newLine();

    for(ComponentInstanceSymbol subcomponent: comp.getSubComponents()){
      builder.append("    ")
        .append("this.")
        .append(subcomponent.getName(), "    ")
        .append(" = new ")
        .append(ComponentHelper.getSubComponentTypeName(subcomponent), "    ")
        .append("(")
        .newLineIfNotEmpty()
        .skipNextSeparator();
      for(String param : helper.getParamValues(subcomponent)) {
        builder.separate(",    ")
          .append(param, "    ")
          .newLineIfNotEmpty();
      }
      builder.append("    ")
        .append(");")
        .newLine();
    }

    builder.append("  ")
      .newLine()
      .append("    ")
      .append("//set up all sub components  ")
      .newLine();
    for(ComponentInstanceSymbol subcomponent: comp.getSubComponents()){
      builder.append("    ")
        .append("this.")
        .append(subcomponent.getName(), "    ")
        .append(".setUp();")
        .newLineIfNotEmpty();
    }
    builder.append("  ")
      .newLine()
      .append("  ")
      .newLine()
      .append("  ")
      .newLine()
      .append("  ")
      .append("// set up output ports")
      .newLine();

    for(PortSymbol portOut: comp.getOutgoingPorts()){
      builder.append("  ")
        .append("this.")
        .append(portOut.getName(), "  ")
        .append(" = new Port<")
        .append(">();")
        .newLineIfNotEmpty();
    }

    builder.append("  ")
      .newLine()
      .newLine()
      .append("  ")
      .newLine()
      .append("    ")
      .append("// propagate children's output ports to own output ports")
      .newLine();
    for(ASTConnector connector: comp.getAstNode().getConnectors()){

      for(ASTPortAccess target: connector.getTargetList()){

        if(!helper.isIncomingPort(comp, connector.getSource(), target, false)) {
          Init.setupPorts(helper, builder, connector, target);
        }
      }
    }
    return builder.append("    ")
      .newLine()
      .append("    ")
      .append("}")
      .newLine()
      .toString();
  }
}