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
    return new MontiArcCoCoChecker()
      .addCoCo(new ComponentTypeNameCapitalization())
      .addCoCo(new FieldNameCapitalization())
      .addCoCo(new GenericTypeParameterNameCapitalization())
      .addCoCo(new InstanceNameCapitalisation())
      .addCoCo(new ParameterNameCapitalization())
      .addCoCo(new PortNameCapitalisation())
      .addCoCo(new ComponentInstanceTypeExists())
      .addCoCo(new InheritedComponentTypeExists())
      .addCoCo(new PortUsage())
      .addCoCo(new SubComponentsConnected())
      .addCoCo(new CircularInheritance())
      .addCoCo(new ConnectorSourceAndTargetComponentDiffer())
      .addCoCo(new ConnectorSourceAndTargetDiffer())
      .addCoCo(new ConnectorSourceAndTargetExistAndFit())
      .addCoCo(new ConfigurationParametersCorrectlyInherited())
      .addCoCo(new InnerComponentNotExtendsDefiningComponent())
      .addCoCo(new FieldTypeExists())
      .addCoCo(new InnerComponentNotExtendsDefiningComponent())
      .addCoCo(new ParameterTypeExists())
      .addCoCo(new PortTypeExists())
      .addCoCo(new PortUniqueSender());
  }
}
