/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import arcbasis.ArcBasisMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import montiarc.util.MontiArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Arrays;
import java.util.regex.Pattern;

public abstract class AbstractTest extends montiarc.util.AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return MontiArcError.ERROR_CODE_PATTERN;
  }

  protected static ASTMCQualifiedName createQualifiedName(@NotNull String... parts) {
    assert parts != null && !Arrays.asList(parts).contains(null);
    return ArcBasisMill.mCQualifiedNameBuilder().setPartsList(Arrays.asList(parts)).build();
  }

  protected static ASTMCQualifiedType createQualifiedType(@NotNull String... parts) {
    assert parts != null && !Arrays.asList(parts).contains(null);
    return ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(createQualifiedName(parts)).build();
  }
}