/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util;

import arcbasis._symboltable.ComponentTypeSymbol;

/**
 * The implementation class for atomic components without specified behavior.
 */

public class AtomicAbstractImplementation {
  public String generateAbstractAtomicImplementation(final ComponentTypeSymbol comp) {
    String generics = Utils.printFormalTypeParameters(comp);
    ConcatenationBuilder builder = new ConcatenationBuilder();
    builder.append(Utils.printPackage(comp))
      .newLineIfNotEmpty()
      .newLine()
      .append("import montiarc.runtimes.timesync.implementation.IComputable;")
      .newLine()
      .append(Utils.printImports(comp))
      .newLineIfNotEmpty()
      .newLine()
      .append("class ")
      .append(comp.getName())
      .append("Impl")
      .append(generics)
      .newLineIfNotEmpty()
      .append("implements IComputable<")
      .append(comp.getName())
      .append("Input")
      .append(generics)
      .append(", ")
      .append(comp.getName())
      .append("Result")
      .append(generics)
      .append("> {")
      .newLineIfNotEmpty()
      .newLine()
      .append("  ")
      .append("public ")
      .append(comp.getName(), "  ")
      .append("Impl(")
      .append(Utils.printConfigurationParametersAsList(comp), "  ")
      .append(")");
    printThrowException("constructor", comp, builder);
    builder.append("  ")
      .append("public ")
      .append(comp.getName(), "  ")
      .append("Result")
      .append(generics, "  ")
      .append(" getInitialValues()");
    printThrowException("getInitialValues()", comp, builder);
    builder.append("  ")
      .append("public ")
      .append(comp.getName(), "  ")
      .append("Result")
      .append(generics, "  ")
      .append(" compute(")
      .append(comp.getName(), "  ")
      .append("Input")
      .append(generics, "  ")
      .append(" ")
      .append(Identifier.getInputName(), "  ")
      .append(")");
    printThrowException("compute()", comp, builder);
    return builder.append("}").newLine().toString();
  }

  private void printThrowException(String methodName, ComponentTypeSymbol comp, ConcatenationBuilder builder) {
    builder.append(" {")
      .newLineIfNotEmpty()
      .append("    ")
      .append("throw new Error(\"Invoking "+methodName+" on abstract implementation ")
      .append(comp.getPackageName(), "    ")
      .append(".")
      .append(comp.getName(), "    ")
      .append("\");")
      .newLineIfNotEmpty()
      .append("  ")
      .append("}")
      .newLine()
      .newLine();
  }
}