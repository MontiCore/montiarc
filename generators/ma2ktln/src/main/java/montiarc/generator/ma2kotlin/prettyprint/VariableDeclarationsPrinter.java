/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTDeclaratorId;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;

import java.util.function.Predicate;

public class VariableDeclarationsPrinter extends MCVarDeclarationStatementsPrettyPrinter {
  public VariableDeclarationsPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTDeclaratorId a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print(a.getName());
    getPrinter().print("Field");
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  @Override
  public void handle(ASTLocalVariableDeclaration a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    boolean separator = false;
    for (ASTVariableDeclarator variable : a.getVariableDeclaratorList()) {
      if (separator) {
        getPrinter().println();
      }
      separator = true;
      a.getMCModifierList().stream().filter(isNotFinal()).forEach(modifier -> {
        modifier.accept(getTraverser());
        getPrinter().print(" ");
      });
      getPrinter().print(a.getMCModifierList().stream().allMatch(isNotFinal()) ? "var" : "val");
      getPrinter().print(" ");
      variable.getDeclarator().accept(getTraverser());
      getPrinter().print(" :");
      a.getMCType().accept(getTraverser());
      if (variable.isPresentVariableInit()) {
        getPrinter().print(" = ");
        variable.getVariableInit().accept(getTraverser());
      }
    }
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }

  public Predicate<ASTMCModifier> isNotFinal() {
    return modifier -> {
      boolean[] isFinal = {false};
      MontiArcTraverser finalFinder = MontiArcMill.traverser();
      finalFinder.add4MCCommonStatements(new MCCommonStatementsVisitor2() {
        @Override
        public void visit(ASTJavaModifier node) {
          if (node.getModifier() == ASTConstantsMCCommonStatements.FINAL) {
            isFinal[0] = true;
          }
        }
      });
      modifier.accept(finalFinder);
      return !isFinal[0];
    };
  }
}
