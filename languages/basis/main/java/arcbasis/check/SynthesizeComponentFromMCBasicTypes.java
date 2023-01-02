/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesHandler;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

/**
 * A visitor (a handler indeed) that creates {@link CompTypeExpression}s from {@link ASTMCQualifiedType}s, given that
 * there is a ComponentTypeSymbol which is resolvable through the name represented by the {@link ASTMCQualifiedType}.
 */
public class SynthesizeComponentFromMCBasicTypes implements MCBasicTypesHandler {

  protected MCBasicTypesTraverser traverser;

  /**
   * Common state with other visitors, if this visitor is part of a visitor composition.
   */
  protected SynthCompTypeResult resultWrapper;

  public SynthesizeComponentFromMCBasicTypes(@NotNull SynthCompTypeResult resultWrapper) {
    this.resultWrapper = Preconditions.checkNotNull(resultWrapper);
  }

  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull MCBasicTypesTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  @Override
  public void handle(@NotNull ASTMCQualifiedType mcType) {
    Preconditions.checkNotNull(mcType);
    Preconditions.checkNotNull(mcType.getEnclosingScope());
    Preconditions.checkArgument(mcType.getEnclosingScope() instanceof IArcBasisScope);

    String compTypeName = mcType.getMCQualifiedName().getQName();
    IArcBasisScope enclScope = ((IArcBasisScope) mcType.getEnclosingScope());
    List<ComponentTypeSymbol> compType = enclScope.resolveComponentTypeMany(compTypeName);

    if (compType.isEmpty()) {
      resultWrapper.setResultAbsent();
    } else {
      resultWrapper.setResult(new TypeExprOfComponent(compType.get(0)));
    }
  }
}
