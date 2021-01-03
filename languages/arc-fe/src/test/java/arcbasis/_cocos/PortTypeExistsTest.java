/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTPortDeclaration;
<<<<<<< HEAD
import arcbasis._symboltable.ArcBasisScope;
=======
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
import arcbasis._symboltable.ArcBasisSymbolTableCreator;
import arcbasis._symboltable.IArcBasisScope;
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
    IArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(scope);
    symTab.handle(ast);
    PortTypeExists coco = new PortTypeExists();
    coco.check(ast.getPorts(0));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_PORT });
  }
}