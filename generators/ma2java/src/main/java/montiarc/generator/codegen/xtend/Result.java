/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import montiarc.generator.codegen.xtend.util.ConcatenationBuilder;
import montiarc.generator.codegen.xtend.util.IMontiArcGenerator;
import montiarc.generator.codegen.xtend.util.Utils;
import montiarc.generator.helper.ComponentHelper;

import java.util.List;

/**
 * Generates the result class for a component.
 */

public class Result implements IMontiArcGenerator {

  @Override
  public String generate(final ComponentTypeSymbol comp) {
    ComponentHelper helper = new ComponentHelper(comp);
    ConcatenationBuilder builder = new ConcatenationBuilder();
    builder.append(Utils.printPackage(comp))
      .newLineIfNotEmpty()
      .newLine()
      .append(Utils.printImports(comp))
      .newLineIfNotEmpty()
      .append("import de.montiarc.runtimes.timesync.implementation.IResult;")
      .newLine()
      .newLine()
      .newLine()
      .append("public class ")
      .append(comp.getName())
      .append("Result")
      .append(Utils.printFormalTypeParameters(comp))
      .append("   ")
      .newLineIfNotEmpty();
    if (comp.isPresentParentComponent()) {
      builder.append(" extends")
        .newLineIfNotEmpty()
        .append(Utils.printSuperClassFQ(comp))
        .append("Result")
        .newLineIfNotEmpty();
      if (comp.getParent().hasTypeParameter()) {
        builder.skipNextSeparator().append("< ");
        for(final String scTypeParams : helper.getSuperCompActualTypeArguments()) {
          builder.separate(",")
            .newLineIfNotEmpty()
            .append(scTypeParams);
        }

        builder.append(">");
      }
      builder.newLineIfNotEmpty();
    }

    builder.append("implements IResult ")
      .newLine()
      .append(" ")
      .append("{")
      .newLine()
      .append("  ")
      .newLine();
    for(final PortSymbol port : comp.getOutgoingPorts()) {
      builder.append("  ");
      builder.append(Utils.printMember(helper.getRealPortTypeString(port), port.getName(), "private"), "  ");
      builder.newLineIfNotEmpty();
    }
    builder.append("  ");
    Input.printDefaultConstructor("Result", comp, builder);
    Input.printParameterizedConstructor("Result", ComponentTypeSymbol::getAllOutgoingPorts, comp.getOutgoingPorts(), comp, helper, builder);

    builder.newLine();
    for(final PortSymbol port : comp.getOutgoingPorts()) {
      String name = port.getName();
      String type = helper.getRealPortTypeString(port);
      builder.append("  ")
        .newLineIfNotEmpty()
        .append("  ")
        .newLineIfNotEmpty()
        .append("  ")
        .append("public void set")
        .appendWithFirstCharUpper(name)
        .append("(")
        .append(type, "  ")
        .append(" ")
        .append(name, "  ")
        .append(") {")
        .newLineIfNotEmpty()
        .append("  ")
        .append("  ")
        .append("this.")
        .append(name, "    ")
        .append(" = ")
        .append(name, "    ")
        .append(";")
        .newLineIfNotEmpty()
        .append("  ")
        .append("}")
        .newLine()
        .append("  ")
        .newLine()
        .append("  ")
        .append("public ")
        .append(type, "  ")
        .append(" get")
        .appendWithFirstCharUpper(name)
        .append("() {")
        .newLineIfNotEmpty()
        .append("  ")
        .append("  ")
        .append("return this.")
        .append(name, "    ")
        .append(";")
        .newLineIfNotEmpty()
        .append("  ")
        .append("}")
        .newLine();
    }
    return printToString(builder, comp.getOutgoingPorts());
  }

  public static String printToString(ConcatenationBuilder builder, List<PortSymbol> ports) {
    Result.beginToString(builder);

    for(PortSymbol port: ports){
      Result.addToToString(builder, port);
    }

    return endToString(builder);
  }

  public static String endToString(ConcatenationBuilder builder) {
    return builder.append("    ")
      .append("return result + \"]\";")
      .newLine()
      .append("  ")
      .append("}  ")
      .newLine()
      .append("  ")
      .newLine()
      .append("} ")
      .newLine()
      .newLine()
      .toString();
  }

  static void addToToString(ConcatenationBuilder builder, PortSymbol port) {
    builder.append("    ")
      .append("result += \"")
      .append(port.getName(), "    ")
      .append(": \" + this.")
      .append(port.getName(), "    ")
      .append(" + \" \";")
      .newLineIfNotEmpty();
  }

  public static void beginToString(ConcatenationBuilder builder) {
    builder.newLine()
      .append("  ")
      .append("@Override")
      .newLine()
      .append("  ")
      .append("public String toString() {")
      .newLine()
      .append("    ")
      .append("String result = \"[\";")
      .newLine();
  }

  @Override
  public String getArtifactName(final ComponentTypeSymbol comp) {
    return comp.getName() + "Result";
  }
}