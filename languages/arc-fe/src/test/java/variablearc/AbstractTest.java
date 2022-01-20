/* (c) https://github.com/MontiCore/monticore */
package variablearc;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;

public class AbstractTest extends arcbasis.AbstractTest {

  @BeforeEach
  public void init() {
    VariableArcMill.globalScope().getRootVariationPoints().clear();
    VariableArcMill.globalScope().clear();
    VariableArcMill.reset();
    VariableArcMill.init();
    addBasicTypes2Scope();
  }

  protected static ASTMCQualifiedName createQualifiedName(@NotNull String... parts) {
    assert parts != null && !Arrays.asList(parts).contains(null);
    return VariableArcMill.mCQualifiedNameBuilder().setPartsList(Arrays.asList(parts)).build();
  }

  protected static ASTMCQualifiedType createQualifiedType(@NotNull String... parts) {
    assert parts != null && !Arrays.asList(parts).contains(null);
    return VariableArcMill.mCQualifiedTypeBuilder().setMCQualifiedName(createQualifiedName(parts)).build();
  }
}