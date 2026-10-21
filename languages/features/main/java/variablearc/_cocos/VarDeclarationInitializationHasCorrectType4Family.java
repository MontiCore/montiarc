/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.check.TypeCheck4Family;

import java.util.List;

/**
 * Checks that the initialization of a variable declaration fits the type of the
 * variable for every combination of the symbols its names may refer to in the
 * configurations in which the declaration is present.
 */
public class VarDeclarationInitializationHasCorrectType4Family extends VarDeclarationInitializationHasCorrectType {

  protected final TypeCheck4Family typeCheck;

  public VarDeclarationInitializationHasCorrectType4Family(@NotNull TypeCheck4Family typeCheck) {
    this.typeCheck = Preconditions.checkNotNull(typeCheck);
  }

  @Override
  public void check(@NotNull ASTVariableDeclarator node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentVariableInit()) {
      super.check(node);
      return;
    }
    this.typeCheck.check(node, node.getEnclosingScope(), List.of(node.getVariableInit()), () -> super.check(node));
  }
}
