/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import montiarc.MontiArcMill;
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
  public int unitTestCaseCount(ASTComponentType node) {
    Preconditions.checkNotNull(node);
    if (!node.isPresentStereotype()) return 0;

    Set<String> names = node.getHead().getArcParameterList().stream().map(ASTArcParameter::getName).collect(Collectors.toSet());
    names.addAll(node.getBody().streamArcElementsOfType(ASTArcFeatureDeclaration.class).flatMap(ASTArcFeatureDeclaration::streamArcFeatures).map(ASTArcFeature::getName).collect(Collectors.toSet()));
    names.add("ticks");

    return Math.max(
      node.getStereotype().getValuesList().stream()
        .filter(sv -> names.contains(sv.getName()))
        .filter(ASTStereoValue::isPresentExpression)
        .filter(stereo -> MontiArcMill.typeDispatcher().isSetExpressionsASTSetEnumeration(stereo.getExpression()) && MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(stereo.getExpression()).isList())
        .map(stereo -> MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(stereo.getExpression()).getSetCollectionItemList().size())
        .reduce(1, Math::max),
      getStereoValue(node, "test").map(stereo -> {
        if (MontiArcMill.typeDispatcher().isSetExpressionsASTSetEnumeration(stereo.getExpression()))
          return MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(stereo.getExpression()).getSetCollectionItemList().size();
        else return 1;
      }).orElse(1)
    );
  }

  public boolean isStereoValueList(ASTStereoValue stereo) {
    return stereo.isPresentExpression()
      && MontiArcMill.typeDispatcher().isSetExpressionsASTSetEnumeration(stereo.getExpression())
      && MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(stereo.getExpression()).isList();
  }

  public Optional<ASTStereoValue> getStereoValue(ASTComponentType comp, String name) {
    return comp.getStereotype().getValuesList().stream().filter(sv -> Objects.equals(name, sv.getName())).filter(ASTStereoValue::isPresentExpression).findAny();
  }

  public boolean isTestSource(ASTComponentType comp) {
    return getStereoValue(comp, "test").isPresent();
  }

  public List<ASTExpression> getTestValues(ASTComponentType comp, int index) {
    Optional<ASTSetEnumeration> testDefinition = getStereoValue(comp, "test")
      .map(ASTStereoValue::getExpression)
      .filter(MontiArcMill.typeDispatcher()::isSetExpressionsASTSetEnumeration)
      .map(MontiArcMill.typeDispatcher()::asSetExpressionsASTSetEnumeration);
    Preconditions.checkArgument(testDefinition.isPresent());

    ArrayList<ASTExpression> testValues = new ArrayList<>();
    for (List<ASTExpression> testCaseDefinition : testDefinition.get().getSetCollectionItemList().stream()
      .map(item -> MontiArcMill.typeDispatcher().asSetExpressionsASTSetEnumeration(MontiArcMill.typeDispatcher().asSetExpressionsASTSetValueItem(item).getExpression()).getSetCollectionItemList())
      .map(list -> list.stream().map(item -> MontiArcMill.typeDispatcher().asSetExpressionsASTSetValueItem(item).getExpression()).collect(Collectors.toList())).collect(Collectors.toList())) {
      if (index < testCaseDefinition.size())
        testValues.add(testCaseDefinition.get(index));
    }
    return testValues;
  }
}