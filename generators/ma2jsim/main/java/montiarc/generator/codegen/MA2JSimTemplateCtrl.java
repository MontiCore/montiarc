/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import com.google.common.base.Preconditions;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import montiarc.util.MASimError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MA2JSimTemplateCtrl extends de.monticore.generating.templateengine.TemplateController {

  public MA2JSimTemplateCtrl(@NotNull GeneratorSetup setup, @NotNull String templateName) {
    super(Preconditions.checkNotNull(setup), Preconditions.checkNotNull(templateName));
  }

  public MA2JSimTemplateCtrl(@NotNull GeneratorSetup setup,
                             @NotNull String templateName,
                             @NotNull List<String> templateBlackList) {
    super(Preconditions.checkNotNull(setup),
          Preconditions.checkNotNull(templateName),
          Preconditions.checkNotNull(templateBlackList)
    );
  }

  public void writeArgs(@NotNull String templateName, @NotNull Formatter formatter, @NotNull Path filePath,
                        @NotNull ASTNode ast, @NotNull List<Object> templateArguments) throws FormatterException {
    Preconditions.checkNotNull(templateName);
    Preconditions.checkNotNull(formatter);
    Preconditions.checkNotNull(filePath);
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(templateArguments);
    Preconditions.checkArgument(!templateName.isEmpty());

    final String qualifiedTemplateName = this.completeQualifiedName(templateName);

    String content = this.processTemplate(templateName, formatter, ast, templateArguments);

    Path completeFilePath;
    if (filePath.isAbsolute()) {
      completeFilePath = filePath;
    } else {
      completeFilePath = Paths.get(config.getOutputDirectory().getAbsolutePath(), filePath.toString());
    }

    Reporting.reportFileCreation(qualifiedTemplateName, filePath, ast);

    FileReaderWriter.storeInFile(completeFilePath, content);

    Log.debug(completeFilePath + " written successfully!", this.getClass().getName());

    Reporting.reportFileFinalization(qualifiedTemplateName, filePath, ast);
  }

  protected String processTemplate(@NotNull String templateName, @NotNull Formatter formatter, @NotNull ASTNode ast,
                                   @NotNull List<Object> templateArguments) throws FormatterException {
    Preconditions.checkNotNull(templateName);
    Preconditions.checkNotNull(formatter);
    Preconditions.checkNotNull(ast);
    Preconditions.checkNotNull(templateArguments);
    Preconditions.checkArgument(!templateName.isEmpty());

    StringBuilder builder = this.includeArgs(templateName, ast, templateArguments);

    if (builder.length() == 0) {
      Log.warn(String.format(MASimError.TEMPLATE_OUTPUT_EMPTY.toString(), this.completeQualifiedName(templateName)));
    }

    if (config.isTracing() && config.getModelName().isPresent()) {
      builder.insert(0, config.getCommentStart() + " generated from model "
          + config.getModelName().get() + " " + config.getCommentEnd() + "\n");
    }

    return this.format(formatter, builder.toString());
  }

  protected String format(@NotNull Formatter formatter, @NotNull String content) throws FormatterException {
    return formatter.formatSource(content);
  }
}