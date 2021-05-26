/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.*;
import de.monticore.types.check.TypeCheckResult;
import genericarc._cocos.GenericTypeParameterNameCapitalization;
import montiarc.check.MontiArcDerive;

/**
 * Bundle of CoCos for the MontiArc language.
 */
public class MontiArcCoCos {

  public static MontiArcCoCoChecker createChecker() {
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();

    //arcbasis cocos
    checker.addCoCo(new CircularInheritance());
    checker.addCoCo(new ComponentInstanceTypeExists());
    checker.addCoCo(new ComponentTypeNameCapitalization());
    checker.addCoCo(new ConfigurationParametersCorrectlyInherited());
    checker.addCoCo(new ConfigurationParameterAssignment(new MontiArcDerive(new TypeCheckResult())));
    checker.addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new FieldTypeExists());
    checker.addCoCo(new InheritedComponentTypeExists());
    checker.addCoCo(new InnerComponentNotExtendsDefiningComponent());
    checker.addCoCo(new InstanceNameCapitalisation());
    checker.addCoCo(new NoSubComponentReferenceCycles());
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new ParameterTypeExists());
    checker.addCoCo(new PortNameCapitalisation());
    checker.addCoCo(new PortTypeExists());
    checker.addCoCo(new PortUniqueSender());
    checker.addCoCo(new PortUsage());
    checker.addCoCo(new SubComponentsConnected());
    checker.addCoCo(new UniqueIdentifierNames());
    
    //genericarc cocos
    checker.addCoCo(new GenericTypeParameterNameCapitalization());

    // statechart origin cocos
    // checker.addCoCo(new UniqueStates()); // uses uml traverser
    // checker.addCoCo(new TransitionSourceTargetExists());
    //checker.addCoCo(new CapitalStateNames()); // this coco is implemented wrongly
    // checker.addCoCo(new PackageCorrespondsToFolders());
    // checker.addCoCo(new SCFileExtension());
    // checker.addCoCo(new SCNameIsArtifactName());

    return checker;
  }
}
