package montiarc.helper;

import com.google.common.base.Preconditions;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.symboltable.types.references.TypeReference;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Pretty print symbols.
 * This class does not use the AST
 *
 * @author Michael von Wenckstern
 * @since 20.05.2016
 */
public class SymbolPrinter {
  
  /**
   * Helper function for nested type arguments such as
   * {@code List<NewType<String, List<String>>>}
   *
   * @param arg The type argument to print
   * @return The printed type argument
   */
  public static String printTypeArgument(ActualTypeArgument arg) {
    Preconditions.checkNotNull(arg);
    if(!arg.getType().existsReferencedSymbol()){
      return "";
    }

    String ret = printWildCardPrefix(arg);
    ret += arg.getType().getReferencedSymbol().getFullName();
    // String ret = arg.getType().getName();
    if (arg.getType().getActualTypeArguments() != null
        && !arg.getType().getActualTypeArguments().isEmpty()) {
      ret += printTypeArguments(arg.getType().getActualTypeArguments());
    }
    return ret;
  }

  /**
   * Print a list of type arguments.
   * Example output: {@code <type1,type2,...>}
   * @param arg The list of type arguments to print
   * @return The printed type arguments, joined with "," and surrounded by "<" and ">"
   */
  public static String printTypeArguments(List<ActualTypeArgument> arg) {
    Preconditions.checkNotNull(arg);
    if (arg.isEmpty())
      return "";
    return "<" + arg.stream()
                     .map(a -> printWildCardPrefix(a)
                                   + printTypeArgument(a)
                                   + printArrayDimensions(a))
                     .collect(Collectors.joining(","))
               + ">";
  }

  /**
   * Print the prefix of a type argument, if it is a bound.
   *
   * @param typeArgument Type argument to check
   * @return "? super " if the type arg is a lower bound, "? extends " if it is
   * an upper bound, the empty String, else.
   */
  public static String printWildCardPrefix(ActualTypeArgument typeArgument) {
    if (typeArgument.isLowerBound()) {
      return "? super ";
    }
    else if (typeArgument.isUpperBound()) {
      return "? extends ";
    }
    return "";
  }

  /**
   * Print the square array brackets according to the dimension {@code n} of
   * the referenced type.
   *
   * @param typeArgument Actual type argument for which to print the dimensionality String
   * @return Empty string, if the type reference is not a {@link JTypeReference},
   * else the String "[]" concatenated {@code n} times.
   */
  protected static String printArrayDimensions(ActualTypeArgument typeArgument) {
    Preconditions.checkNotNull(typeArgument);
    if(!typeArgument.getType().existsReferencedSymbol()){
      return "";
    }
    return printArrayDimensions(typeArgument.getType());
  }

  /**
   * Print the square array brackets according to the dimension {@code n} of
   * the typeReference.
   *
   * @param typeReference Actual type argument for which to print the dimensionality String
   * @return Empty string, if the type reference is not a {@link JTypeReference},
   * else the String "[]" concatenated {@code n} times.
   */
  public static String printArrayDimensions(TypeReference<?> typeReference){
    int dim = typeReference.getDimension();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < dim; i++) {
      sb.append("[]");
    }
    return sb.toString();
  }

  /**
   * Helper function for nested type arguments such as
   * {@code List<NewType<String, List<String>>>}
   *
   * @param arg The type symbol to print.
   * @return The printed symbol.
   */
  public static String printTypeWithFormalTypeParameters(JTypeSymbol arg) {
    String ret = arg.getName();
    
    if (!arg.getSuperTypes().isEmpty()) {
      ret += " extends " + arg.getSuperTypes().stream()
          .map(t -> t.getReferencedSymbol().getFullName()
              + printTypeArguments(t.getActualTypeArguments()))
          .collect(Collectors.joining("&"));
    }
    ret += printFormalTypeParameters(arg.getFormalTypeParameters());
    return ret;
  }
  
  /**
   * @param arg A list of type symbols to print
   * @return string representation of the type parameters associated with this port.
   */
  public static String printFormalTypeParameters(List<? extends JTypeSymbol> arg) {
    if (arg.isEmpty())
      return "";
    return "<" + arg.stream()
                     .map(a -> printTypeWithFormalTypeParameters(a))
                     .collect(Collectors.joining(","))
               + ">";
  }

  /**
   *
   * @param port The port which should be printed
   * @param ip IndentPrinter used to print the result
   */
  public static void printPort(PortSymbol port, IndentPrinter ip) {
    if (port.isIncoming()) {
      ip.print("in ");
    }
    else {
      ip.print("out ");
    }
    ip.print(port.getTypeReference().getName());
    ip.print(printTypeArguments(port.getTypeReference().getActualTypeArguments()));
    ip.print(" ");
    ip.print(port.getName());
  }

  /**
   * Print a PortSymbol to String
   * @param port The port to print
   * @return The printed port
   */
  public static String printPort(PortSymbol port) {
    IndentPrinter ip = new IndentPrinter();
    printPort(port, ip);
    return ip.getContent();
  }

  /**
   * Prints a connector using the given IndentPrinter.
   * @param connectorSymbol The connector to print
   * @param ip The {@link IndentPrinter} used to print the connector
   */
  public static void printConnector(ConnectorSymbol connectorSymbol, IndentPrinter ip) {
    ip.print(connectorSymbol.getSource());
    ip.print(" -> ");
    ip.print(connectorSymbol.getTarget());
  }

  /**
   * Prints the given connector as a String.
   * @param con The connector to print
   * @return The printed connector
   */
  public static String printConnector(ConnectorSymbol con) {
    IndentPrinter ip = new IndentPrinter();
    printConnector(con, ip);
    return ip.getContent();
  }

  /**
   * Prints a list of ASTExpressions to String. The elements are joined with ","
   * and the list of elements is surrounded by opening and closing brackets.
   * Results look like {@code (param1,param2,param3,...)}
   *
   * @param config The list of configuration arguments to print.
   * @return The printed argument list, surrounded by brackets
   */
  public static String printConfigArguments(List<ASTExpression> config) {
    if (config.isEmpty())
      return "";
    JavaDSLPrettyPrinter javaPrinter = new JavaDSLPrettyPrinter(new IndentPrinter());
    return "(" + config.stream()
                     .map(javaPrinter::prettyprint)
                     .collect(Collectors.joining(","))
               + ")";
  }

  /**
   * Print the given expression
   * @param config The expression to print
   * @return The printed expression
   */
  public static String printConfigArgument(ASTExpression config) {
    
    JavaDSLPrettyPrinter javaPrinter = new JavaDSLPrettyPrinter(new IndentPrinter());
    return javaPrinter.prettyprint(config);
  }

  /**
   * Print a component instance to the given IndentPrinter
   * @param inst The component instance to print
   * @param ip The printer to print with
   */
  public static void printComponentInstance(ComponentInstanceSymbol inst, IndentPrinter ip) {
    ip.print(inst.getComponentType().getName());
    ip.print(printTypeArguments(inst.getComponentType().getActualTypeArguments()));
    ip.print(printConfigArguments(inst.getConfigArguments()));
    ip.print(" ");
    ip.print(inst.getName());
  }

  /**
   * Print a component instance as a String.
   * @param inst The instance to print.
   * @return The printed instance
   */
  public static String printComponentInstance(ComponentInstanceSymbol inst) {
    IndentPrinter ip = new IndentPrinter();
    printComponentInstance(inst, ip);
    return ip.getContent();
  }

  /**
   * Print a collection of Ports to the given IndentPrinter.
   * The output is similar to the following:
   * <pre>
   * {@code
   * port
   *    in port1,
   *    out port2
   *    ...
   *    out portN;
   * }
   * </pre>
   * @param ports The collection of Ports to print
   * @param ip The printer to print the list to
   */
  public static void printPorts(Collection<PortSymbol> ports, IndentPrinter ip) {
    if (!ports.isEmpty()) {
      ip.println("port");
      ip.indent();
      int i = 0;
      int s = ports.size();
      for (PortSymbol p : ports) {
        printPort(p, ip);
        if (i == s - 1) {
          ip.println(";");
        }
        else {
          ip.println(",");
        }
        i++;
      }
      ip.unindent();
    }
  }

  /**
   * Print a component symbol to the given IndentPrinter
   * @param cmp The component symbol to print
   * @param ip The printer to print to
   * @param skipPackageImport Whether to skip printing package and imports
   *                          of the component
   */
  public static void printComponent(ComponentSymbol cmp, IndentPrinter ip,
      boolean skipPackageImport) {
    if (!skipPackageImport) {
      if (cmp.getPackageName() != null && !cmp.getPackageName().isEmpty()) {
        ip.print("package ");
        ip.print(cmp.getPackageName());
        ip.println(";");
      }
      if (cmp.getImports() != null) {
        cmp.getImports().stream().forEachOrdered(
            a -> ip.println("import " + a.getStatement() + (a.isStar() ? ".*" : "") + ";"));
      }
    }
    ip.print("component " + cmp.getName());
    if (cmp.hasFormalTypeParameters()) {
      ip.print(printFormalTypeParameters(cmp.getFormalTypeParameters()));
    }
    if (cmp.hasConfigParameters()) {
      ip.print("(");
      ip.print(cmp.getConfigParameters().stream()
          .map(a -> a.getType().getName() + " " + a.getName()).collect(Collectors.joining(",")));
      ip.print(")");
    }
    ip.println(" {");
    
    ip.indent();
    
    printPorts(cmp.getPorts(), ip);
    
    cmp.getSubComponents().stream().forEachOrdered(a -> {
      ip.print("component ");
      printComponentInstance(a, ip);
      ip.println(";");
    });
    
    cmp.getInnerComponents().stream().forEachOrdered(a -> printComponent(a, ip, true));
    
    cmp.getConnectors().stream().forEachOrdered(a -> {
      ip.print("connect ");
      printConnector(a, ip);
      ip.println(";");
    });
    
    ip.unindent();
    ip.println("}");
  }

  /**
   * Print a component Symbol
   * @param cmp The componentSymbol to print
   * @return The printed component
   */
  public static String printComponent(ComponentSymbol cmp) {
    IndentPrinter ip = new IndentPrinter();
    printComponent(cmp, ip, false);
    return ip.getContent();
  }
  
}
