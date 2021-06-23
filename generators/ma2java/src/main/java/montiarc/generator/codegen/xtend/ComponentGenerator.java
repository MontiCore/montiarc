/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import java.util.ArrayList;
import java.util.List;

import montiarc.generator.codegen.xtend.util.*;
import montiarc.generator.helper.ComponentHelper;

/**
 * Generates the component class for atomic and composed components.
 */

public class ComponentGenerator implements IMontiArcGenerator {
  protected String generics;
  
  protected ComponentHelper helper;
  
  public Setup setup = new Setup();
  
  public Ports ports = new Ports();
  
  public Init init = new Init();
  
  public Update update = new Update();
  
  public Subcomponents subcomponents = new Subcomponents();
  
  @Override
  public String generate(final ComponentTypeSymbol comp) {
    this.generics = Utils.printFormalTypeParameters(comp);
    this.helper = new ComponentHelper(comp);
    ConcatenationBuilder builder = new ConcatenationBuilder()
      .append(Utils.printPackage(comp))
      .newLineIfNotEmpty()
      .newLine()
      .append(Utils.printImports(comp))
      .newLineIfNotEmpty()
      .append("import ")
      .append(Utils.printPackageWithoutKeyWordAndSemicolon(comp))
      .append(".")
      .append(comp.getName())
      .append("Input;")
      .newLineIfNotEmpty()
      .append("import ")
      .append(Utils.printPackageWithoutKeyWordAndSemicolon(comp))
      .append(".")
      .append(comp.getName())
      .append("Result;")
      .newLineIfNotEmpty()
      .append("import de.montiarc.runtimes.timesync.delegation.IComponent;")
      .newLine()
      .append("import de.montiarc.runtimes.timesync.delegation.Port;")
      .newLine()
      .append("import de.montiarc.runtimes.timesync.implementation.IComputable;")
      .newLine()
      .append("import de.montiarc.runtimes.Log;")
      .newLine()
      .newLine()
      .append("public class ")
      .append(comp.getName())
      .append(this.generics)
      .append("      ")
      .newLineIfNotEmpty()
      .append("  ");

    if(comp.isPresentParentComponent()) {
      builder.append(" extends ")
        .append(Utils.printSuperClassFQ(comp), "  ")
        .append(" ")
        .newLineIfNotEmpty()
        .append("  ")
        .append("    ");

      if(comp.getParent().hasTypeParameter()) {

        builder.skipNextSeparator().append("<");
        for(final String scTypeParams : this.helper.getSuperCompActualTypeArguments()) {
          builder.separate(",")
                 .newLineIfNotEmpty()
                 .append("  ")
                 .append("    ")
                 .append(scTypeParams, "      ");
        }

        builder.append(">").newLineIfNotEmpty();
      }

    }

    builder.append("  ")
      .append("implements IComponent {")
      .newLine()
      .append("    ")
      .newLine()
      .append("  ")
      .append("//ports")
      .newLine()
      .append("  ")
      .append(this.ports.print(comp.getPorts()), "  ")
      .newLineIfNotEmpty()
      .append("  ")
      .newLine()
      .append("  ")
      .append("// component variables")
      .newLine()
      .append("  ")
      .append(Utils.printVariables(comp), "  ")
      .newLineIfNotEmpty()
      .append("  ")
      .newLine()
      .append("  ")
      .append("// config parameters")
      .newLine()
      .append("  ")
      .append(Utils.printConfigParameters(comp), "  ")
      .newLineIfNotEmpty()
      .newLine();
    {
      if(comp.isDecomposed()) {
        builder.append("  ")
         .append("// subcomponents")
         .newLine()
         .append("  ")
         .append(this.subcomponents.print(comp), "  ")
         .newLineIfNotEmpty()
         .append("  ")
         .append(this.printComputeComposed(comp), "  ")
         .newLineIfNotEmpty()
         .append("  ")
         .newLine();
      } else {
        builder.append("  ")
          .append("// the components behavior implementation")
          .newLine()
          .append("  ")
          .append("private final IComputable<")
          .append(comp.getName(), "  ")
          .append("Input")
          .append(this.generics, "  ")
          .append(", ")
          .append(comp.getName(), "  ")
          .append("Result")
          .append(this.generics, "  ")
          .append("> ")
          .append(Identifier.getBehaviorImplName(), "  ")
          .append(";")
          .newLineIfNotEmpty()
          .append("  ")
          .newLine()
          .append("  ")
          .append(this.printComputeAtomic(comp), "  ")
          .newLineIfNotEmpty()
          .append("  ")
          .append("private void initialize() {")
          .newLine()
          .append("  ")
          .append("  ")
          .append("// get initial values from behavior implementation")
          .newLine()
          .append("  ")
          .append("  ")
          .append("final ")
          .append(comp.getName(), "    ")
          .append("Result")
          .append(this.generics, "    ")
          .append(" result = ")
          .append(Identifier.getBehaviorImplName(), "    ")
          .append(".getInitialValues();")
          .newLineIfNotEmpty()
          .append("  ")
          .append("  ")
          .newLine()
          .append("  ")
          .append("  ")
          .append("// set results to ports")
          .newLine()
          .append("  ")
          .append("  ")
          .append("setResult(result);")
          .newLine()
          .append("  ")
          .append("  ")
          .append("this.update();")
          .newLine()
          .append("  ")
          .append("}")
          .newLine()
          .append("  ")
          .append("private void setResult(")
          .append(comp.getName(), "  ")
          .append("Result")
          .append(this.generics, "  ")
          .append(" result) {")
          .newLineIfNotEmpty();

        for(PortSymbol portOut: comp.getOutgoingPorts()){
          builder.append("  ")
            .append("  ")
            .append("this.getPort")
            .appendWithFirstCharUpper(portOut.getName())
            .append("().setNextValue(result.get")
            .appendWithFirstCharUpper(portOut.getName())
            .append("());")
            .newLineIfNotEmpty();
        }

        builder.append("  ").append("}").newLine();
      }
    }
    return builder.append("  ")
      .newLine()
      .append("  ")
      .append(this.setup.print(comp), "  ")
      .newLineIfNotEmpty()
      .append("  ")
      .newLine()
      .append("  ")
      .append(this.init.print(comp), "  ")
      .newLineIfNotEmpty()
      .append("  ")
      .newLine()
      .append("  ")
      .append(this.update.print(comp), "  ")
      .newLineIfNotEmpty()
      .append("  ")
      .newLine()
      .append("  ")
      .append(this.printConstructor(comp), "  ")
      .newLineIfNotEmpty()
      .append("  ")
      .newLine()
      .append("  ")
      .append("}")
      .newLine()
      .append("  ")
      .newLine()
      .toString();
  }
  
  public String printConstructor(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder()
      .append("public ")
      .append(comp.getName())
      .append("(")
      .append(Utils.printConfigurationParametersAsList(comp))
      .append(") {")
      .newLineIfNotEmpty();
    if(comp.isPresentParentComponent()) {
      builder.append("  ");
      builder.append("super(");

      builder.skipNextSeparator();
      for(final String inhParam : ComponentGenerator.getInheritedParams(comp)) {
        builder.separate(", ")
          .append(inhParam, "  ")
          .append(" ");
      }
      builder.append(");").newLineIfNotEmpty();
    }
    builder.append("  ").newLine();

    if(comp.isAtomic()) {
      builder.append("  ")
        .append(Identifier.getBehaviorImplName(), "  ")
        .append(" = new ")
        .append(comp.getName(), "  ")
        .append("Impl")
        .append(this.generics, "  ")
        .append("(")
        .newLineIfNotEmpty();

      if(comp.hasParameters()) {
        builder.skipNextSeparator();
        for(final VariableSymbol param : comp.getParameters()) {
          builder.separate(", ")
            .append(param.getName(), "  ")
            .newLineIfNotEmpty();
        }

        builder.append("          ");
      }

      builder.append(");").newLineIfNotEmpty();
    }

    builder.append("  ")
      .append("// config parameters       ")
      .newLine();
    for(VariableSymbol param: comp.getParameters()){
      builder.append("  ")
        .append("this.")
        .append(param.getName(), "  ")
        .append(" = ")
        .append(param.getName(), "  ")
        .append(";")
        .newLineIfNotEmpty();
    }
    return builder.append("}").newLine().toString();
  }
  
  public String printComputeAtomic(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder()
      .append("@Override")
      .newLine()
      .append("public void compute() {")
      .newLine()
      .append("// collect current input port values")
      .newLine()
      .append("final ")
      .append(comp.getName())
      .append("Input")
      .append(this.generics)
      .append(" input = new ")
      .append(comp.getName())
      .append("Input")
      .append(this.generics)
      .newLineIfNotEmpty()
      .append("(");
      builder.skipNextSeparator();
    for(final PortSymbol inPort : comp.getAllIncomingPorts()) {
      builder.separate(",")
        .append("this.getPort")
        .appendWithFirstCharUpper(inPort.getName())
        .append("().getCurrentValue()");
    }
    return builder.append(");")
      .newLineIfNotEmpty()
      .newLine()
      .append("try {")
      .newLine()
      .append("// perform calculations")
      .newLine()
      .append("  ")
      .append("final ")
      .append(comp.getName(), "  ")
      .append("Result")
      .append(this.generics, "  ")
      .append(" result = ")
      .append(Identifier.getBehaviorImplName(), "  ")
      .append(".compute(input); ")
      .newLineIfNotEmpty()
      .append("  ")
      .newLine()
      .append("  ")
      .append("// set results to ports")
      .newLine()
      .append("  ")
      .append("setResult(result);")
      .newLine()
      .append("  ")
      .append("} catch (Exception e) {")
      .newLine()
      .append("Log.error(\"")
      .append(comp.getName())
      .append("\", e);")
      .newLineIfNotEmpty()
      .append("  ")
      .append("}")
      .newLine()
      .append("}")
      .newLine()
      .toString();
  }
  
  public String printComputeComposed(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder()
      .append("@Override")
      .newLine()
      .append("public void compute() {")
      .newLine()
      .append("// trigger computation in all subcomponent instances")
      .newLine();

    for(ComponentInstanceSymbol subcomponent: comp.getSubComponents()){
      builder.append("  ");
      builder.append("this.");
      builder.append(subcomponent.getName(), "  ");
      builder.append(".compute();");
      builder.newLineIfNotEmpty();
    }

    return builder.append("}").newLine().toString();
  }
  
  protected static List<String> getInheritedParams(final ComponentTypeSymbol component) {
    List<String> result = new ArrayList<>();
    List<VariableSymbol> configParameters = component.getParameters();
    if(component.isPresentParentComponent()) {
      ComponentTypeSymbol superCompReference = component.getParent();
      List<VariableSymbol> superConfigParams = superCompReference.getParameters();
      if(!configParameters.isEmpty()) {
        for (int i = 0; (i < superConfigParams.size()); i++) {
          result.add(configParameters.get(i).getName());
        }
      }
    }
    return result;
  }
  
  @Override
  public String getArtifactName(final ComponentTypeSymbol comp) {
    return comp.getName();
  }
}