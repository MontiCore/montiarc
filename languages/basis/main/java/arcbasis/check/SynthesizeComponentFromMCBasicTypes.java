/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesHandler;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

/**
 * A visitor (a handler indeed) that creates {@link CompTypeExpression}s from an
 * {@link de.monticore.types.mcbasictypes._ast.ASTMCType}s, given that there is a ComponentTypeSymbol which is
 * resolvable through the name represented by the {@link ASTMCQualifiedType}.
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
  public void handle(@NotNull ASTMCPrimitiveType node) {
    Preconditions.checkNotNull(node);

    IArcBasisScope enclScope = ((IArcBasisScope) node.getEnclosingScope());
    List<ComponentTypeSymbol> comp = enclScope.resolveComponentTypeMany(node.printType());

    if (comp.isEmpty()) {
      Log.error(ArcError.MISSING_COMPONENT.format(node.printType()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
      resultWrapper.setResultAbsent();
    } else if (comp.size() > 1) {
      Log.error(ArcError.AMBIGUOUS_REFERENCE.format(comp.get(0).getFullName(), comp.get(1).getFullName()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
      resultWrapper.setResult(new TypeExprOfComponent(comp.get(0)));
    } else {
      resultWrapper.setResult(new TypeExprOfComponent(comp.get(0)));
    }
  }

  @Override
  public void handle(@NotNull ASTMCQualifiedType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkArgument(node.getEnclosingScope() instanceof IArcBasisScope);

    IArcBasisScope enclScope = ((IArcBasisScope) node.getEnclosingScope());
    List<ComponentTypeSymbol> comp = enclScope.resolveComponentTypeMany(node.getMCQualifiedName().getQName());

    if (comp.isEmpty()) {
      Log.error(ArcError.MISSING_COMPONENT.format(node.getMCQualifiedName().getQName()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
      resultWrapper.setResultAbsent();
    } else if (comp.size() > 1) {
      Log.error(ArcError.AMBIGUOUS_REFERENCE.format(comp.get(0).getFullName(), comp.get(1).getFullName()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
      resultWrapper.setResult(new TypeExprOfComponent(comp.get(0)));
    } else {
      resultWrapper.setResult(new TypeExprOfComponent(comp.get(0)));
    }
  }
}
