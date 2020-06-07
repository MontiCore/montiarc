/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._symboltable.ArcBasisScope;
import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcBasisSymbolTableCreator;
import arcbasis.util.ArcError;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link ComponentInstanceTypeExists}.
 */
public class ComponentInstanceTypeExistsTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldNotFindType() {
    ASTMCQualifiedType type = arcbasis.ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(
      arcbasis.ArcBasisMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A")).build())
      .build();
    ASTComponentInstantiation ast = arcbasis.ArcBasisMill.componentInstantiationBuilder()
      .setMCType(type).setComponentInstanceList("sub1", "sub2", "sub3").build();
    ArcBasisScope scope = ArcBasisMill.arcBasisScopeBuilder().build();
    ArcBasisSymbolTableCreator symTab = new ArcBasisSymbolTableCreator(scope);
    symTab.handle(ast);
    ComponentInstanceTypeExists coco = new ComponentInstanceTypeExists();
    coco.check(ast.getComponentInstance(0));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_COMPONENT_INSTANCE });
  }
}