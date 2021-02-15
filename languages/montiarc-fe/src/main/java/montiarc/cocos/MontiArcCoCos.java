/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import arcbasis._cocos.*;
import genericarc._cocos.GenericTypeParameterNameCapitalization;
import montiarc._cocos.MontiArcCoCoChecker;

/**
 * Bundle of CoCos for the MontiArc language.
 */
public class MontiArcCoCos {

  public static MontiArcCoCoChecker createChecker() {
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentTypeNameCapitalization());
    checker.addCoCo(new FieldNameCapitalization());
    checker.addCoCo(new GenericTypeParameterNameCapitalization());
    checker.addCoCo(new InstanceNameCapitalisation());
    checker.addCoCo(new ParameterNameCapitalization());
    checker.addCoCo(new PortNameCapitalisation());
    checker.addCoCo(new ComponentInstanceTypeExists());
    checker.addCoCo(new InheritedComponentTypeExists());
    checker.addCoCo(new PortUsage());
    checker.addCoCo(new SubComponentsConnected());
    checker.addCoCo(new CircularInheritance());
    checker.addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetDiffer());
    checker.addCoCo(new ConnectorSourceAndTargetExistAndFit());
    checker.addCoCo(new ConfigurationParametersCorrectlyInherited());
    checker.addCoCo(new InnerComponentNotExtendsDefiningComponent());
    checker.addCoCo(new FieldTypeExists());
    checker.addCoCo(new InnerComponentNotExtendsDefiningComponent());
    checker.addCoCo(new ParameterTypeExists());
    checker.addCoCo(new PortTypeExists());
    checker.addCoCo(new PortUniqueSender());
    return checker;
  }
}
