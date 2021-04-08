/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.AbstractTest;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

/**
 * Holds tests for the handwritten methods of {@link ComponentInstanceTypeExists}.
 */
public class ComponentInstanceTypeExistsTest extends AbstractTest {

  @Test
  public void shouldNotFindType() {
    ASTMCQualifiedType type = createQualifiedType("A");
    ASTComponentInstantiation ast = arcbasis.ArcBasisMill.componentInstantiationBuilder()
      .setMCType(type).setComponentInstanceList("sub1", "sub2", "sub3").build();
    ArcBasisScopesGenitorDelegator symTab = new ArcBasisScopesGenitorDelegator();
    symTab.createFromAST(ast).setName("Scopy");
    ComponentInstanceTypeExists coco = new ComponentInstanceTypeExists();
    coco.check(ast.getComponentInstance(0));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_COMPONENT_INSTANCE });
  }
}