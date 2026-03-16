/* (c) https://github.com/MontiCore/monticore */
package montiarcwithcd._lsp.plugin;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MontiArcWithCDStreamConnectionProvider extends MontiArcWithCDStreamConnectionProviderTOP {

  public MontiArcWithCDStreamConnectionProvider(Project project, String jarName, String pluginId) {
    super(project, jarName, pluginId);
  }

  @Override
  protected @NotNull List<String> createJavaArgs(ConnectionConfig connectionConfig, String javaCmd) {
    List<String> res = new ArrayList<>(super.createJavaArgs(connectionConfig, javaCmd));
    res.add("-mup");
    return res;
  }
}
