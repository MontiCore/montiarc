/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import arcbasis._cocos.*;
import montiarc._cocos.MontiArcCoCoChecker;

/**
 * Bundle of CoCos for the MontiArc language.
 */
public class MontiArcCoCos {

  public static MontiArcCoCoChecker createChecker() {
    return new MontiArcCoCoChecker()
      .addCoCo(new PortUsage())
      .addCoCo(new SubComponentsConnected())
      .addCoCo(new NamesCorrectlyCapitalized())
      .addCoCo(new CircularInheritance())
      .addCoCo(new NamesCorrectlyCapitalized())
      .addCoCo(new ConnectorSourceAndTargetComponentDiffer())
      .addCoCo(new ConnectorSourceAndTargetExistAndFit())
      .addCoCo(new ConfigurationParametersCorrectlyInherited())
      .addCoCo(new InnerComponentNotExtendsDefiningComponent())
      .addCoCo(new ComponentInstanceTypeExists())
      .addCoCo(new FieldTypeExists())
      .addCoCo(new InheritedComponentTypeExists())
      .addCoCo(new InnerComponentNotExtendsDefiningComponent())
      .addCoCo(new ParameterTypeExists())
      .addCoCo(new PortUniqueSender());
  }
}
