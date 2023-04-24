/* (c) https://github.com/MontiCore/monticore */
package arcbasis.trafo;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Separates {@link ASTComponentInstance}s declared together with the definition of their {@link ASTComponentType} into
 * their own {@link ASTComponentInstantiation} declaration.
 * <p>
 * Example:
 * <pre>
 *   component Comp {
 *     component Inner foo, bar { }
 *   }
 * </pre>
 * is transformed into:
 * <pre>
 *   component Comp {
 *     Inner foo, bar;
 *     component Inner { }
 *   }
 * </pre>
 */
public class SeparateCompInstantiationFromTypeDeclTrafo implements ArcBasisVisitor2 {

  @Override
  public void visit(@NotNull ASTComponentBody body) {
    Preconditions.checkNotNull(body);

    for (ASTComponentType cType : body.getElementsOfType(ASTComponentType.class)) {
      List<ASTComponentInstance> instances = cType.getComponentInstanceList();
      if (!instances.isEmpty()) {
        // Create new instantiation declaration and add it to the component body
        ASTComponentInstantiation instDecl = ArcBasisMill.componentInstantiationBuilder()
          .setMCType(buildMCTypeFrom(cType))
          .setComponentInstancesList(instances)
          .set_SourcePositionStart(instances.get(0).get_SourcePositionStart())
          .set_SourcePositionEnd(instances.get(instances.size() - 1).get_SourcePositionEnd())
          .build();

        body.addArcElement(instDecl);

        // Remove instances from its type declaration's list
        cType.setComponentInstanceList(new ArrayList<>());
      }
    }
  }

  /**
   * @return an {@link ASTMCType} with the simple name of the {@code compType}.
   */
  protected ASTMCType buildMCTypeFrom(ASTComponentType compType) {
    SourcePosition startPos = SourcePositionUtil.elongate(
      compType.get_SourcePositionStart(),
      "component ".length()
    );
    SourcePosition endPos = SourcePositionUtil.elongate(
      compType.get_SourcePositionStart(),
      "component ".length() + compType.getName().length()
    );

    ASTMCQualifiedName asName = ArcBasisMill.mCQualifiedNameBuilder()
      .addParts(compType.getName())
      .set_SourcePositionStart(startPos)
      .set_SourcePositionEnd(endPos)
      .build();

    return ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(asName)
      .set_SourcePositionStart(startPos.clone())
      .set_SourcePositionEnd(endPos.clone())
      .build();
  }
}
