/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import de.monticore.ast.ASTCNode;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.nio.file.Path;
import java.util.Arrays;

public class GeneratorEngine extends de.monticore.generating.GeneratorEngine {

  public GeneratorEngine(@NotNull GeneratorSetup gs) {
    super(Preconditions.checkNotNull(gs));
  }

  protected GeneratorSetup getSetup() {
    return (GeneratorSetup) this.setup;
  }

  /**
   * Processes the template <code>templateName</code> with the <code>node</code> and the given
   * <code>templateArguments</code>, formats the content with the provided <code>formatter</code>, and writes the
   * content into the <code>filePath</code>. Unless absolute, the <code>filePath</code> is relative to the configured
   * output directory specified in the {@link GeneratorSetup}.
   *
   * @param templateName      the template to be processed
   * @param formatter         the formatter to use for formatting the content
   * @param filePath          the file path in which the content is to be written
   * @param node              the ast node
   * @param templateArguments additional template arguments (if needed).
   * @throws FormatterException if the content could not be parsed by the java parser of the provided formatter
   * @see this#generate(String, Path, ASTNode, Object...)
   */
  public void generate(@NotNull String templateName, @NotNull Formatter formatter, @NotNull Path filePath,
                       @NotNull ASTNode node, @NotNull Object... templateArguments) throws FormatterException {
    Preconditions.checkNotNull(templateName);
    Preconditions.checkNotNull(formatter);
    Preconditions.checkNotNull(filePath);
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(templateArguments);
    Preconditions.checkArgument(!templateName.isEmpty());
    Preconditions.checkArgument(!filePath.toString().isEmpty());
    TemplateController tc = getSetup().getNewTemplateController(templateName);
    tc.writeArgs(templateName, formatter, filePath, node, Arrays.asList(templateArguments));
  }

  /**
   * Processes the template <code>templateName</code> and the given <code>templateArguments</code>, formats the content
   * with the provided <code>formatter</code>, and writes the content into the <code>filePath</code>. Unless absolute,
   * the <code>filePath</code> is relative to the configured output directory specified in the {@link GeneratorSetup}.
   *
   * @param templateName      the template to be processed
   * @param formatter         the formatter to use for formatting the content
   * @param filePath          the file path in which the content is to be written
   * @param templateArguments additional template arguments (if needed).
   * @throws FormatterException if the content could not be parsed by the java parser of the provided formatter
   * @see this#generateNoA(String, Path, Object...)
   */
  public void generateNoA(@NotNull String templateName, @NotNull Formatter formatter, @NotNull Path filePath,
                          @NotNull Object... templateArguments) throws FormatterException {
    this.generate(templateName, formatter, filePath, this.createDummyAST(), Arrays.asList(templateArguments));
  }

  protected ASTCNode createDummyAST() {
    return new ASTCNode() {

      @Override
      public IScope getEnclosingScope() {
        return null;
      }

      @Override
      public ASTNode deepClone() {
        return this;
      }
    };
  }
}