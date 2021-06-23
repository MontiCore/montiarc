/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.xtend.util;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ImportStatement;
import montiarc.generator.helper.ComponentHelper;

/**
 * This class contains several methods commonly used by generator classes.
 */

public class Utils {
  /**
   * Prints the component's configuration parameters as a comma separated list.
   */
  public static String printConfigurationParametersAsList(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder();
    builder.skipNextSeparator();
    for(final VariableSymbol param : comp.getParameters()) {
      builder.separate(", ")
        .append(" ")
        .append(ComponentHelper.print(param.getType()))
        .append(" ")
        .append(param.getName())
        .append(" ");
    }
    return builder.newLineIfNotEmpty().toString();
  }
  
  /**
   * Prints the component's imports
   */
  public static String printImports(final ComponentTypeSymbol comp) {
  ConcatenationBuilder builder = new ConcatenationBuilder();
    for(final ImportStatement statement : ComponentHelper.getImports(comp)) {
      builder.append("import ").append(statement.getStatement());

      if(statement.isStar()) {
        builder.append(".*");
      }

      builder.append(";").newLineIfNotEmpty();
    }
    for(ComponentTypeSymbol inner: comp.getInnerComponents()){
      builder.append("import ")
        .append((Utils.printPackageWithoutKeyWordAndSemicolon(inner) + "." + inner.getName()))
        .append(";")
        .newLineIfNotEmpty();
    }
    return builder.toString();
  }
  
  /**
   * Prints a member of given visibility name and type
   */
  public static String printMember(final String type, final String name, final String visibility) {
    ConcatenationBuilder builder = new ConcatenationBuilder()
      .append(visibility)
      .append(" ")
      .append(type)
      .append(" ")
      .append(name)
      .append(";")
      .newLineIfNotEmpty();
    return builder.toString();
  }
  
  /**
   * Prints members for configuration parameters.
   */
  public static String printConfigParameters(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder();

    for(VariableSymbol param: comp.getParameters()){
      builder.append(Utils.printMember(ComponentHelper.print(param.getType()), param.getName(), "private final"));
      builder.newLineIfNotEmpty();
    }

    return builder.toString();
  }
  
  /**
   * Prints members for variables
   */
  public static String printVariables(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder();
    for(VariableSymbol variable: comp.getFields()){
      if(!comp.getParameters().contains(variable)) {
        builder.append(Utils.printMember(ComponentHelper.print(variable.getType()), variable.getName(), "protected"));
        builder.newLineIfNotEmpty();
      }
    }
    return builder.toString();
  }
  
  /**
   * Prints formal parameters of a component.
   */
  public static String printFormalTypeParameters(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder();
    {
      if(comp.hasTypeParameter()) {
        builder.append("<").newLine();
        builder.skipNextSeparator();
        for(final TypeVarSymbol generic : comp.getTypeParameters()) {
          builder.separate(", ")
            .append(generic.getName(), "  ")
            .newLineIfNotEmpty();
        }
        builder.append(">").newLine();
      }
    }
    return builder.toString();
  }
  
  /**
   * Print the package declaration for generated component classes.
   * Uses recursive determination of the package name to accomodate for components
   * with at least two levels of inner component. These require changing the package name
   * to avoid name clashes between the generated packages and the outermost component.
   */
  public static String printPackage(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder();
    builder.append("package ");

    if(comp.isInnerComponent()) {
      builder.append((Utils.printPackageWithoutKeyWordAndSemicolon(comp.getOuterComponent().get()) + "."+comp.getOuterComponent().get().getName()+ "gen"));
    } else {
      builder.append(comp.getPackageName());
    }

    builder.append(";");
    builder.newLineIfNotEmpty();
    return builder.toString();
  }
  
  /**
   * Helper function used to determine package names.
   */
  public static String printPackageWithoutKeyWordAndSemicolon(final ComponentTypeSymbol comp) {
    ConcatenationBuilder builder = new ConcatenationBuilder();
    if(comp.isInnerComponent()) {
      builder.append((Utils.printPackageWithoutKeyWordAndSemicolon(comp.getOuterComponent().get()) + "." + comp.getOuterComponent().get().getName() + "gen"));
    } else {
      builder.append(comp.getPackageName());
    }
    builder.newLineIfNotEmpty();
    return builder.toString();
  }
  
  public static String printSuperClassFQ(final ComponentTypeSymbol comp) {
    String packageName = Utils.printPackageWithoutKeyWordAndSemicolon(comp.getParent());
    if(packageName.equals("")) {
      return comp.getParent().getName();
    } else {
      return new ConcatenationBuilder()
        .append(packageName)
        .append(".")
        .append(comp.getParent().getName())
        .toString();
    }
  }
}