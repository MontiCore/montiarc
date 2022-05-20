/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

/**
 * the $-sign has a special meaning in kotlin-literals which is why it needs special care taken
 */
public class CommonLiteralsPrinter extends MCCommonLiteralsPrettyPrinter {

  public CommonLiteralsPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void visit(ASTStringLiteral string) {
    printer.print("\"" + string.getSource().replaceAll("(\\$)", "$1{'$1'}") + "\"");
  }
}
