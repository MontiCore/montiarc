/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

public class GeneratorSetup extends de.monticore.generating.GeneratorSetup {

  @Override
  public TemplateController getNewTemplateController(@NotNull String templateName) {
    Preconditions.checkNotNull(templateName);
    Preconditions.checkArgument(!templateName.isEmpty());
    return new TemplateController(this, templateName);
  }

  public TemplateController getNewTemplateController(@NotNull String templateName,
                                                     @NotNull List<String> templateBlacklist) {
    Preconditions.checkNotNull(templateName);
    Preconditions.checkNotNull(templateBlacklist);
    Preconditions.checkArgument(!templateName.isEmpty());
    return new TemplateController(this, templateName, templateBlacklist);
  }
}