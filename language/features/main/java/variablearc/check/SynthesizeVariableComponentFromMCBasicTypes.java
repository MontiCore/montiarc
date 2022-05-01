/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.SynthCompTypeResult;
import arcbasis.check.SynthesizeComponentFromMCBasicTypes;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;

import java.util.Optional;

public class SynthesizeVariableComponentFromMCBasicTypes extends SynthesizeComponentFromMCBasicTypes {

  public SynthesizeVariableComponentFromMCBasicTypes(SynthCompTypeResult resultWrapper) {
    super(resultWrapper);
  }

  @Override
  public void handle(@NotNull ASTMCQualifiedType mcType) {
    Preconditions.checkNotNull(mcType);
    Preconditions.checkNotNull(mcType.getEnclosingScope());
    Preconditions.checkArgument(mcType.getEnclosingScope() instanceof IVariableArcScope);

    String compTypeName = mcType.getMCQualifiedName().getQName();
    IVariableArcScope enclScope = ((IVariableArcScope) mcType.getEnclosingScope());
    Optional<ComponentTypeSymbol> compType = enclScope.resolveComponentType(compTypeName);

    if (!compType.isPresent()) {
      Log.error(ArcError.SYMBOL_NOT_FOUND.format(compTypeName), mcType.get_SourcePositionStart());
      resultWrapper.setCurrentResultAbsent();
    }
    else {
      resultWrapper.setCurrentResult(new TypeExprOfVariableComponent(compType.get()));
    }
  }
}
