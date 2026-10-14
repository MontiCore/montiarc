/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._ast.ASTFDCompilationUnitBuilder;
import de.monticore.featurediagram._ast.ASTFDElement;
import de.monticore.featurediagram._ast.ASTFeatureDiagram;
import de.monticore.featurediagram._ast.ASTFeatureGroup;
import de.monticore.featurediagram._ast.ASTGroupPart;
import montiarc.arc2fd.model.FDData;
import montiarc.arc2fd.model.FeatureGroup;
import montiarc.arc2fd.model.FeaturePair;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the AST of a feature diagram from {@link FDData}.
 */
public class FeatureDiagramBuilder {

  /**
   * @param fdData Data to build the feature diagram from
   * @param pkg    Package of the feature diagram, empty for the default package
   * @return <code>AST</code> of a feature diagram
   */
  public ASTFDCompilationUnit build(@NotNull FDData fdData, @NotNull List<String> pkg) {
    Preconditions.checkNotNull(fdData);
    Preconditions.checkNotNull(pkg);

    List<ASTFDElement> elements = new ArrayList<>();

    // and-groups
    fdData.andGroups().forEach((parent, features) -> {
      if (!features.isEmpty()) {
        List<ASTGroupPart> parts = features.stream().map(f -> groupPart(f.name(), f.optional())).toList();
        elements.add(featureTreeRule(parent, FeatureDiagramMill.andGroupBuilder().addAllGroupParts(parts).build()));
      }
    });

    // or-/xor-groups
    for (FeatureGroup group : fdData.groups()) {
      List<ASTGroupPart> parts = group.features().stream().map(f -> groupPart(f, false)).toList();
      ASTFeatureGroup fdGroup = group.xor()
        ? FeatureDiagramMill.xorGroupBuilder().addAllGroupParts(parts).build()
        : FeatureDiagramMill.orGroupBuilder().addAllGroupParts(parts).build();
      elements.add(featureTreeRule(group.parent(), fdGroup));
    }

    // cross-tree constraints
    for (FeaturePair pair : fdData.requires()) {
      elements.add(FeatureDiagramMill.featureConstraintBuilder()
        .setConstraint(FeatureDiagramMill.requiresBuilder()
          .setLeft(nameExpression(pair.left()))
          .setOperator("requires")
          .setRight(nameExpression(pair.right()))
          .build())
        .build());
    }
    for (FeaturePair pair : fdData.excludes()) {
      elements.add(FeatureDiagramMill.featureConstraintBuilder()
        .setConstraint(FeatureDiagramMill.excludesBuilder()
          .setLeft(nameExpression(pair.left()))
          .setOperator("excludes")
          .setRight(nameExpression(pair.right()))
          .build())
        .build());
    }

    ASTFeatureDiagram fd = FeatureDiagramMill.featureDiagramBuilder()
      .setName(fdData.root())
      .setFDElementsList(elements)
      .build();

    ASTFDCompilationUnitBuilder builder = FeatureDiagramMill.fDCompilationUnitBuilder().setFeatureDiagram(fd);

    if (!pkg.isEmpty())
      builder.setPackage(FeatureDiagramMill.mCQualifiedNameBuilder().setPartsList(pkg).build());

    return builder.build();
  }

  protected static ASTFDElement featureTreeRule(@NotNull String parent, @NotNull ASTFeatureGroup group) {
    return FeatureDiagramMill.featureTreeRuleBuilder().setName(parent).setFeatureGroup(group).build();
  }

  protected static ASTGroupPart groupPart(@NotNull String feature, boolean optional) {
    return FeatureDiagramMill.groupPartBuilder().setName(feature).setOptional(optional).build();
  }

  protected static ASTExpression nameExpression(@NotNull String name) {
    return ExpressionsBasisMill.nameExpressionBuilder().setName(name).build();
  }
}
