/* (c) https://github.com/MontiCore/monticore */
package montiarcwithcd4a._lsp.plugin;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MontiArcWithCD4AStreamConnectionProvider extends MontiArcWithCD4AStreamConnectionProviderTOP {

  public MontiArcWithCD4AStreamConnectionProvider(Project project, String jarName) {
    super(project, jarName);
  }

  @Override
  protected @NotNull List<String> createJavaArgs(ConnectionConfig connectionConfig, String javaCmd) {
    List<String> res = new ArrayList<>(super.createJavaArgs(connectionConfig, javaCmd));
    res.add("-mup");
    return res;
  }
}
