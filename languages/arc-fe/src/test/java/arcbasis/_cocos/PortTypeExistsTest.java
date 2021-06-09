/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

/**
 * Holds tests for the handwritten methods of {@link PortTypeExists}.
 */
public class PortTypeExistsTest extends AbstractTest {

  @Test
  public void shouldNotFindType() {
    ASTMCQualifiedType type = createQualifiedType("Integer");
    ASTPortDeclaration ast = arcbasis.ArcBasisMill.portDeclarationBuilder().setIncoming(true)
      .setMCType(type).setPortList("p1", "p2", "p3").build();
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(arcbasis.ArcBasisMill.componentInterfaceBuilder().addPortDeclaration(ast).build())
        .setName("I am Scope");
    PortTypeExists coco = new PortTypeExists();
    coco.check(ast.getPort(0));
    this.checkOnlyExpectedErrorsPresent(ArcError.MISSING_TYPE_OF_PORT);
  }
}