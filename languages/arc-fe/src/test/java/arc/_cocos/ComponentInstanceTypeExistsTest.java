/* (c) https://github.com/MontiCore/monticore */
package arc._cocos;

import arc._ast.ASTComponentInstantiation;
import arc._ast.ArcMill;
import arc._symboltable.ArcScope;
import arc._symboltable.ArcSymTabMill;
import arc._symboltable.ArcSymbolTableCreator;
import arc.util.ArcError;
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
    ASTMCQualifiedType type = ArcMill.mCQualifiedTypeBuilder().setMCQualifiedName(
      ArcMill.mCQualifiedNameBuilder().setPartList(Collections.singletonList("A")).build()).build();
    ASTComponentInstantiation ast = ArcMill.componentInstantiationBuilder()
      .setType(type).setInstanceList("sub1", "sub2", "sub3").build();
    ArcScope scope = ArcSymTabMill.arcScopeBuilder().build();
    ArcSymbolTableCreator symTab = new ArcSymbolTableCreator(scope);
    symTab.handle(ast);
    ComponentInstanceTypeExists coco = new ComponentInstanceTypeExists();
    coco.check(ast.getInstance(0));
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(),
      new ArcError[] { ArcError.MISSING_TYPE_OF_COMPONENT_INSTANCE });
  }
}