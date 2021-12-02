/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.*;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import genericarc._cocos.GenericTypeParameterNameCapitalization;
import montiarc._cocos.util.CheckTypeExistence4MontiArc;
import montiarc._cocos.util.PortReferenceExtractor4CommonExpressions;
import montiarc.check.MontiArcDeriveType;

/**
 * Bundle of CoCos for the MontiArc language.
 */
public class MontiArcCoCos {

  public static MontiArcCoCoChecker createChecker() {
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();

    // ArcBasis CoCos
    checker.addCoCo(new CircularInheritance());
    checker.addCoCo(new ComponentInstanceTypeExists(new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter())));
    checker.addCoCo(new ComponentTypeNameCapitalization());
    checker.addCoCo(new ConfigurationParametersCorrectlyInherited());
    checker.addCoCo(new ConfigurationParameterAssignment(new MontiArcDeriveType(new TypeCheckResult())));
    checker.addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetDirectionsFit());
    checker.addCoCo(new ConnectorSourceAndTargetExist());
    checker.addCoCo(new ConnectorSourceAndTargetTypesFit());
    checker.addCoCo(new FieldInitExpressionTypesCorrect(new MontiArcDeriveType(new TypeCheckResult())));
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new FieldInitExpressionsOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new FieldTypeExists(new CheckTypeExistence4MontiArc()));
    checker.addCoCo(new InheritedComponentTypeExists());
    checker.addCoCo(new InnerComponentNotExtendsDefiningComponent());
    checker.addCoCo(new InstanceNameCapitalisation());
    checker.addCoCo(new NoSubComponentReferenceCycles());
    checker.addCoCo(new ParameterDefaultValuesOmitPortReferences(new PortReferenceExtractor4CommonExpressions()));
    checker.addCoCo(new ParameterDefaultValueTypesCorrect(new MontiArcDeriveType(new TypeCheckResult())));
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new ParameterTypeExists(new CheckTypeExistence4MontiArc()));
    checker.addCoCo(new PortNameCapitalisation());
    checker.addCoCo(new PortTypeExists(new CheckTypeExistence4MontiArc()));
    checker.addCoCo(new PortUniqueSender());
    checker.addCoCo(new PortUsage());
    checker.addCoCo(new SubComponentsConnected());
    checker.addCoCo(new UniqueIdentifierNames());
    
    // GenericArc CoCos
    checker.addCoCo(new GenericTypeParameterNameCapitalization());

    // MontiArc CoCos
    checker.addCoCo(new RootComponentTypesNoInstanceName());

    return checker;
  }
}
