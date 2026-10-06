/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTArcComponentType;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
import de.monticore.ocl.setexpressions._ast.ASTSetValueItem;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import variablearc._ast.ASTArcFeature;
import variablearc._ast.ASTArcFeatureDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class MaUnitHelper {

  /**
   * @return the number of test cases defined by the component
   */
  public int unitTestCaseCount(ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentStereotype()) return 0;

    Set<String> names = node.getHead().getArcParameterList().stream().map(ASTArcParameter::getName).collect(Collectors.toSet());
    names.addAll(node.getBody().streamArcElementsOfType(ASTArcFeatureDeclaration.class).flatMap(ASTArcFeatureDeclaration::streamArcFeatures).map(ASTArcFeature::getName).collect(Collectors.toSet()));
    names.add("ticks");
    names.add("simulatedTickLength");

    return Math.max(
      node.getStereotype().getValuesList().stream()
        .filter(sv -> names.contains(sv.getName()))
        .filter(ASTStereoValue::isPresentExpression)
        .map(ASTStereoValue::getExpression)
        .filter(ASTSetEnumeration.class::isInstance)
        .map(ASTSetEnumeration.class::cast)
        .filter(ASTSetEnumeration::isList)
        .map(enumeration -> enumeration.getSetCollectionItemList().size())
        .reduce(1, Math::max),
      getStereoValue(node, "test").map(stereo -> {
        if (stereo.getExpression() instanceof ASTSetEnumeration enumeration)
          return enumeration.getSetCollectionItemList().size();
        else return 1;
      }).orElse(1)
    );
  }

  public boolean isStereoValueList(ASTStereoValue stereo) {
    return stereo.isPresentExpression()
      && stereo.getExpression() instanceof ASTSetEnumeration enumeration
      && enumeration.isList();
  }

  public Optional<ASTStereoValue> getStereoValue(ASTArcComponentType comp, String name) {
    return comp.getStereotype().getValuesList().stream().filter(sv -> Objects.equals(name, sv.getName())).filter(ASTStereoValue::isPresentExpression).findAny();
  }

  public boolean isTestSource(ASTArcComponentType comp) {
    return getStereoValue(comp, "test").isPresent();
  }

  public List<ASTExpression> getTestValues(ASTArcComponentType comp, int index) {
    Optional<ASTSetEnumeration> testDefinition = getStereoValue(comp, "test")
      .map(ASTStereoValue::getExpression)
      .filter(ASTSetEnumeration.class::isInstance)
      .map(ASTSetEnumeration.class::cast);
    Preconditions.checkArgument(testDefinition.isPresent());

    ArrayList<ASTExpression> testValues = new ArrayList<>();
    for (List<ASTExpression> testCaseDefinition : testDefinition.get().getSetCollectionItemList().stream()
      .map(ASTSetValueItem.class::cast)
      .map(ASTSetValueItem::getExpression)
      .map(ASTSetEnumeration.class::cast)
      .map(ASTSetEnumeration::getSetCollectionItemList)
      .map(list -> list.stream()
        .map(ASTSetValueItem.class::cast)
        .map(ASTSetValueItem::getExpression).collect(Collectors.toList())).toList()) {
      if (index < testCaseDefinition.size())
        testValues.add(testCaseDefinition.get(index));
    }
    return testValues;
  }
}
