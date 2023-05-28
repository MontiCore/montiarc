/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.dse;

import de.monticore.literals.mccommonliterals._prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class DseMCCommonLiteralsPrettyPrinter extends MCCommonLiteralsPrettyPrinter {

  protected boolean printComments;

  public DseMCCommonLiteralsPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
    this.printComments = printComments;
  }

  @Override
  public void handle(de.monticore.literals.mccommonliterals._ast.ASTNatLiteral node) {

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("ctx.mkInt(" + node.getDigits() + ")");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral node) {

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.getSource() == de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals.TRUE) {
      getPrinter().print("ctx.mkBool(true) ");
    }
    else if (node.getSource() == de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals.FALSE) {
      getPrinter().print("ctx.mkBool(false) ");
    }
    else {
      montiarc.rte.log.Log.error("the boolean literal is neither true nor false");
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.literals.mccommonliterals._ast.ASTStringLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("ctx.mkString(\"" + node.getSource() + "\")");
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    if (node.isPresentDigits()) {
      getPrinter().print("ctx.mkInt(");
      if (node.isNegative()) {
        getPrinter().print("-");
      }
      getPrinter().print(node.getDigits() + " ");
      getPrinter().print(")");
    } else {
      montiarc.rte.log.Log.error("the signed natural does not contain any digits");
    }
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.literals.mccommonliterals._ast.ASTCharLiteral node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print("ctx.charFromBv(ctx.mkBV('" + node.getValue() + "', 18))");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral node) {

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    printReal(node.getPre() + "." + node.getPost());

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral node) {

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    printReal("-" + node.getPre() + "." + node.getPost());

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral node) {

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    printReal(node.getPre() + "." + node.getPost());
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void handle(de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral node) {

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    printReal("-" + node.getPre() + "." + node.getPost());

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  public void printReal(String number) {
    getPrinter().print("ctx.mkReal(\"" + number + "\")");

  }
}