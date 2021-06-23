/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend;

import arcbasis._symboltable.ComponentTypeSymbol;
import montiarc.generator.codegen.xtend.util.ConcatenationBuilder;
import montiarc.generator.codegen.xtend.util.IMontiArcGenerator;
import montiarc.generator.codegen.xtend.util.Utils;

/**
 * Generates the deployment class for a component.
 */

public class Deploy implements IMontiArcGenerator {
  @Override
  public String generate(final ComponentTypeSymbol comp) {
    String name = comp.getName();
    return new ConcatenationBuilder()
      .append(Utils.printPackage(comp))
      .newLineIfNotEmpty()
      .newLine()
      .append("public class Deploy")
      .append(name)
      .append(" {")
      .newLineIfNotEmpty()
      .append("  ")
      .append("final static int CYCLE_TIME = 50; // in ms")
      .newLine()
      .append("    ")
      .newLine()
      .append("  ")
      .append("public static void main(String[] args) {")
      .newLine()
      .append("    ")
      .append("final ")
      .append(name, "    ")
      .append(" compy = new ")
      .append(name, "    ")
      .append("();")
      .newLineIfNotEmpty()
      .append("    ")
      .newLine()
      .append("    ")
      .append("compy.setUp();")
      .newLine()
      .append("    ")
      .append("compy.init();")
      .newLine()
      .append("             ")
      .newLine()
      .append("    ")
      .append("long time;")
      .newLine()
      .append("       ")
      .newLine()
      .append("    ")
      .append("while (!Thread.interrupted()) {")
      .newLine()
      .append("      ")
      .append("time = System.currentTimeMillis();")
      .newLine()
      .append("      ")
      .append("compy.compute();")
      .newLine()
      .append("      ")
      .append("// update after compute")
      .newLine()
      .append("      ")
      .append("compy.update();")
      .newLine()
      .append("      ")
      .append("while((System.currentTimeMillis()-time) < CYCLE_TIME){")
      .newLine()
      .append("        ")
      .append("Thread.yield();")
      .newLine()
      .append("      ")
      .append("}")
      .newLine()
      .append("    ")
      .append("}")
      .newLine()
      .append("  ")
      .append("}")
      .newLine()
      .append("}")
      .newLine()
      .toString();
  }
  
  @Override
  public String getArtifactName(final ComponentTypeSymbol comp) {
    return ("Deploy" + comp.getName());
  }
}