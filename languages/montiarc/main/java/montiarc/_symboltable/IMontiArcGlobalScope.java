/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import org.codehaus.commons.nullanalysis.NotNull;

public interface IMontiArcGlobalScope extends IMontiArcGlobalScopeTOP {

  void loadFileForModelName(@NotNull String modelName);

  void loadFile(@NotNull String file);
}