/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import arcbasis._ast.ASTPortAccess;
import arcbasis._ast.ASTPortAccessBuilder;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import montiarc.generator.ma2kotlin.codegen.TypeTrimTool;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * contains some helping methods
 */
public class SymbolPrinter {
  protected final IndentPrinter printer;

  public SymbolPrinter(IndentPrinter printer) {
    this.printer = Preconditions.checkNotNull(printer);
  }

  protected IndentPrinter getPrinter() {
    return printer;
  }

  /**
   * removes block-and line comments from the expression, if present
   *
   * @param expression any text that might contain java-styled-comments
   * @return the same expression but with no comments guaranteed
   */
  protected String clearComments(String expression) {
    return expression.replaceAll("/\\*(.|\\n)*?\\*/|//.*?$", "");
  }

  /**
   * tries to find variables and ports with the given name and delegates them to the given consumers
   *
   * @param scope  scope in which ports and variables
   * @param name   name that might be referencing a special symbol
   * @param ports  consumer for ports, if <code>null</code>, those symbols won't be handled
   * @param fields consumer for fields and parameters, if <code>null</code>, those symbols won't be handled
   * @return <code>true</code> if anything was delegated to any consumer. <code>false</code>, if no symbol was found or no consumer accepted it
   */
  protected boolean findAndConsumeSymbol(IExpressionsBasisScope scope, String name, Consumer<ASTPortAccess> ports, Consumer<VariableSymbol> fields) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(scope);
    Preconditions.checkArgument(scope instanceof IArcBasisScope);
    IArcBasisScope arcScope = (IArcBasisScope) scope;
    if (!name.matches("[\\w\\s.]+")) {
      return false;
    }
    if (arcScope.resolvePort(name).isPresent() && ports != null) {
      ASTPortAccess access = new ASTPortAccessBuilder().setQualifiedName(name).build();
      access.setEnclosingScope(scope);
      ports.accept(access);
      return true;
    }
    Optional<VariableSymbol> field = arcScope.resolveVariable(name);
    if (field.isPresent() && fields != null) {
      field.ifPresent(fields);
      return true;
    }
    return false;
  }

  /**
   * prints a statement that references a variable-variable in the kotlin kode
   *
   * @param field the variable that is referenced here
   */
  protected void printVariable(VariableSymbol field) {
    Preconditions.checkNotNull(field);
    // add a postfix, since that is also used in the templates
    getPrinter().print(field.getName() + "Field");
  }

  /**
   * prints an expression that retrieves the latest value of an input port (without a suspending call)
   *
   * @param port the port to print
   */
  protected void printPortQuery(ASTPortAccess port) {
    Preconditions.checkNotNull(port);
    getPrinter().print("(event[");
    printPort(port);
    getPrinter().print("]?.payload as ");
    getPrinter().print(new TypeTrimTool().printType(port.getPortSymbol().getType()));
    getPrinter().print(")");
  }

  /**
   * appends a kotlin expression that references the given port
   *
   * @param port the port to print here
   */
  protected void printPort(ASTPortAccess port) {
    Preconditions.checkNotNull(port);
    String original = port.getQName();
    IntStream.range(0, original.length())
        .filter(i -> original.charAt(i) == '.')
        .mapToObj(i -> original.substring(0, i))
        .map(s -> s.substring(s.lastIndexOf('.') + 1))
        .forEachOrdered(name -> {
          getPrinter().print("getSubcomponent(\"");
          getPrinter().print(name.trim());
          getPrinter().print("\").");
        });
    getPrinter().print("get");
    getPrinter().print(port.getPortSymbol().isIncoming() ? "Input" : "Output");
    getPrinter().print("Port(\"");
    getPrinter().print(port.getPort());
    getPrinter().print("\")");
  }
}
