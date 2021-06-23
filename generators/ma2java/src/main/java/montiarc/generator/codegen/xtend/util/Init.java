/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import montiarc.generator.helper.ComponentHelper;

/**
 * Class responsible for printing the init() method for both atomic and composed components.
 */

public class Init {
  /**
   * Delegates to the right printInit method.
   */
  public String print(final ComponentTypeSymbol comp) {
    if(comp.isAtomic()) {
      return this.printInitAtomic(comp);
    } else {
      return this.printInitComposed(comp);
    }
  }
  
  private String printInitAtomic(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder();
    printInit(comp, builder);
    builder.append("}");
    builder.newLine();
    return builder.toString();
  }

  private String printInitComposed(final ComponentTypeSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    ConcatenationBuilder builder = new ConcatenationBuilder();
    printInit(comp, builder);
    builder.newLine()
      .append("// connect outputs of children with inputs of children, by giving ")
      .newLine()
      .append("// the inputs a reference to the sending ports")
      .newLine();

    for(ASTConnector connector: comp.getAstNode().getConnectors()){

      for(ASTPortAccess target: connector.getTargetList()){

        if(helper.isIncomingPort(comp, connector.getSource(), target, false)) {
          setupPorts(helper, builder, connector, target);
        }
      }
    }

    builder.newLine()
      .append("// init all subcomponents")
      .newLine()
      .newLine();

    for(ComponentInstanceSymbol subcomponent: comp.getSubComponents()){
      builder.append("this.")
        .append(subcomponent.getName())
        .append(".init();")
        .newLineIfNotEmpty();
    }

    builder.append("}");
    builder.newLine();
    return builder.toString();
  }

  static void setupPorts(ComponentHelper helper, ConcatenationBuilder builder, ASTConnector connector, ASTPortAccess target) {
    builder.append(helper.getConnectorComponentName(connector.getSource(), target, false))
      .append(".setPort")
      .appendWithFirstCharUpper(helper.getConnectorPortName(connector.getSource(), target, false))
      .append("(")
      .append(helper.getConnectorComponentName(connector.getSource(), target, true))
      .append(".getPort")
      .appendWithFirstCharUpper(helper.getConnectorPortName(connector.getSource(), target, true))
      .append("());")
      .newLineIfNotEmpty();
  }

  private void printInit(ComponentTypeSymbol comp, ConcatenationBuilder builder) {
    builder.append("@Override");
    builder.newLine();
    builder.append("public void init() {");
    builder.newLine();

    if(comp.isPresentParentComponent()) {
      builder.append("super.init();");
      builder.newLine();
    }

    builder.append("// set up unused input ports");
    builder.newLine();

    for(PortSymbol portIn: comp.getIncomingPorts()){
      builder.append("if (this.")
        .append(portIn.getName())
        .append(" == null) {")
        .newLineIfNotEmpty()
        .append("  ")
        .append("this.")
        .append(portIn.getName(), "  ")
        .append(" = Port.EMPTY;")
        .newLineIfNotEmpty()
        .append("}")
        .newLine();
    }

  }
}