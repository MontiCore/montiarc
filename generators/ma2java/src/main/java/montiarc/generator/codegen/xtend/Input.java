/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;

import java.util.List;
import java.util.function.Function;

import montiarc.generator.codegen.xtend.util.ConcatenationBuilder;
import montiarc.generator.codegen.xtend.util.IMontiArcGenerator;
import montiarc.generator.codegen.xtend.util.Utils;
import montiarc.generator.helper.ComponentHelper;

/**
 * Generates the input class for a component.
 */

public class Input implements IMontiArcGenerator {
  @Override
  public String generate(final ComponentTypeSymbol comp) {
    ComponentHelper helper;
    helper = new ComponentHelper(comp);
    ConcatenationBuilder builder = new ConcatenationBuilder()
      .append(Utils.printPackage(comp))
      .newLineIfNotEmpty()
      .newLine()
      .append(Utils.printImports(comp))
      .newLineIfNotEmpty()
      .append("import de.montiarc.runtimes.timesync.implementation.IInput;")
      .newLine()
      .newLine()
      .newLine()
      .append("public class ")
      .append(comp.getName())
      .append("Input")
      .append(Utils.printFormalTypeParameters(comp))
      .newLineIfNotEmpty();

    if(comp.isPresentParentComponent()) {
      builder.append(" extends")
        .newLineIfNotEmpty()
        .append("      ")
        .append(Utils.printSuperClassFQ(comp), "      ")
        .append("Input")
        .newLineIfNotEmpty()
        .append("      ");

      if(comp.getParent().hasTypeParameter()) {
        builder.append("<");
        builder.newLineIfNotEmpty();

        builder.skipNextSeparator();
        for(final String scTypeParams : helper.getSuperCompActualTypeArguments()) {
          builder.separate(", ")
            .append("      ")
            .append(scTypeParams, "      ")
            .newLineIfNotEmpty()
            .append("      ");
        }

        builder.append(" > ");
      }

      builder.newLineIfNotEmpty();
    }

    builder.append("implements IInput ")
      .newLine()
      .append(" ")
      .append("{")
      .newLine()
      .append("  ")
      .newLine();

    for(PortSymbol port: comp.getIncomingPorts()){
      builder.append("  ");
      builder.append(Utils.printMember(helper.getRealPortTypeString(port), port.getName(), "protected"), "  ");
      builder.newLineIfNotEmpty();
    }

    builder.append("  ");
    printDefaultConstructor("Input", comp, builder);
    printParameterizedConstructor("Input", ComponentTypeSymbol::getAllIncomingPorts, comp.getIncomingPorts(), comp, helper, builder);
    for(final PortSymbol port : comp.getIncomingPorts()) {
      builder.append("  ")
        .append("public ")
        .append(helper.getRealPortTypeString(port), "  ")
        .append(" get")
        .appendWithFirstCharUpper(port.getName())
        .append("() {")
        .newLineIfNotEmpty()
        .append("  ")
        .append("  ")
        .append("return this.")
        .append(port.getName(), "    ")
        .append(";")
        .newLineIfNotEmpty()
        .append("  ")
        .append("}")
        .newLine();
    }
    return Result.printToString(builder, comp.getIncomingPorts());
  }

  static void printPortsAsParameters(ComponentHelper helper, ConcatenationBuilder builder, List<PortSymbol> ports) {
    builder.skipNextSeparator();
    for(PortSymbol port:ports){
      builder.separate(", ")
        .append(helper.getRealPortTypeString(port), "  ")
        .append(" ")
        .append(port.getName(), "  ")
        .append(" ");
    }
  }

  static void initializePorts(ConcatenationBuilder builder, List<PortSymbol> ports) {
    for(PortSymbol port: ports){
      builder.append("  ")
        .append("  ")
        .append("this.")
        .append(port.getName(), "    ")
        .append(" = ")
        .append(port.getName(), "    ")
        .append("; ")
        .newLineIfNotEmpty();
    }
    builder.append("  ")
      .append("  ")
      .newLine();
  }

  public static void printDefaultConstructor(String inputResult, ComponentTypeSymbol comp, ConcatenationBuilder builder){
    builder.newLine()
      .append("  ")
      .append("/**")
      .newLine()
      .append("  * creates an empty "+inputResult+" where all values are <code>null</code>.")
      .newLine()
      .append("  */")
      .newLine()
      .append("public ")
      .append(comp.getName())
      .append(inputResult)
      .append("() {")
      .newLineIfNotEmpty();
    if (comp.isPresentParentComponent()) {
      builder.append("    ");
      builder.append("super();");
      builder.newLine();
    }
    builder.append("  ")
      .append("}")
      .newLine()
      .append("  ")
      .newLine();
  }

  public static void printParameterizedConstructor(String inputResult, Function<ComponentTypeSymbol, List<PortSymbol>> getAllPorts, List<PortSymbol> basicPorts, ComponentTypeSymbol comp, ComponentHelper helper, ConcatenationBuilder builder){
    if (!getAllPorts.apply(comp).isEmpty()) {
      builder.append("  ")
        .append("public ")
        .append(comp.getName()+inputResult, "  ")
        .append("(");

      Input.printPortsAsParameters(helper, builder, getAllPorts.apply(comp));

      builder.append(") {");
      builder.newLineIfNotEmpty();
      if (comp.isPresentParentComponent()) {
        builder.append("  ")
          .append("  ")
          .append("super(");
        for(final PortSymbol port : getAllPorts.apply(comp.getParent())) {
          builder.append(" ")
            .append(port.getName(), "    ")
            .newLineIfNotEmpty()
            .append("  ")
            .append("  ");
        }
        builder.append(");");
        builder.newLineIfNotEmpty();
      }
      Input.initializePorts(builder, basicPorts);
      builder.append("  ")
        .append("}")
        .newLine();
    }
  }

  @Override
  public String getArtifactName(final ComponentTypeSymbol comp) {
    return (comp.getName()+ "Input");
  }
}