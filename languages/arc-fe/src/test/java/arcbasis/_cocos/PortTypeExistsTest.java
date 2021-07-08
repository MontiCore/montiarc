/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Holds tests for the handwritten methods of {@link PortTypeExists}.
 */
public class PortTypeExistsTest extends AbstractTest {

  @Test
  public void shouldNotFindType() {
    ASTMCQualifiedType type = createQualifiedType("Integer");
    ASTPortDeclaration portDec = arcbasis.ArcBasisMill.portDeclarationBuilder().setIncoming(true)
      .setMCType(type).setPortList("p1", "p2", "p3").build();
    ASTComponentType ast = ArcBasisMill.componentTypeBuilder()
      .setName("CompA").setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder()
        .addArcElement(ArcBasisMill.componentInterfaceBuilder()
          .addPortDeclaration(portDec)
          .build())
        .build())
      .build();
    ArcBasisScopesGenitorDelegator symTab = ArcBasisMill.scopesGenitorDelegator();
    symTab.createFromAST(ast);
    PortTypeExists coco = new PortTypeExists();
    coco.check(portDec.getPort(0));
    this.checkOnlyExpectedErrorsPresent(ArcError.MISSING_TYPE_OF_PORT);
  }
}