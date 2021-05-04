/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.github.javaparser.JavaParser;
import com.google.common.base.Preconditions;
import de.monticore.cd4code.CD4CodeMill;
import org.apache.commons.io.FileUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

public class GeneratorTest {

  @BeforeEach
  public void init() {
    CD4CodeMill.globalScope().clear();
    CD4CodeMill.reset();
    CD4CodeMill.init();
  }

  protected static final String TEST_RESOURCE_PATH = "src/test/resources";
  protected static final String TEST_JAVA_PATH = "src/test/java";
  protected static final String TEST_TARGET_PATH = "target/generated-test-sources";

  protected JavaParser parser;

  protected JavaParser getJavaParser() {
    if (this.parser == null) {
      this.setJavaParser(new JavaParser());
    }
    return this.parser;
  }

  protected void setJavaParser(@NotNull JavaParser parser) {
    Preconditions.checkNotNull(parser);
    this.parser = parser;
  }

  @BeforeAll
  public static void cleanTarget() throws IOException {
    FileUtils.cleanDirectory(Paths.get(TEST_TARGET_PATH).toFile());
  }

  @ParameterizedTest
  @ValueSource(strings = {"models/simple", "models/domain"})
  public void quickGeneratorTest(@NotNull String modelPath) {
    //Given
    POJOGeneratorTool tool = new POJOGeneratorTool(Paths.get(TEST_TARGET_PATH), Paths.get(TEST_JAVA_PATH));

    //When
    tool.generateCDTypesInPath(Paths.get(TEST_RESOURCE_PATH, modelPath));

    //Then
    Collection<File> result = FileUtils.listFiles(Paths.get(TEST_TARGET_PATH).toFile(), new String[]{"java"}, true);
    Assertions.assertFalse(result.isEmpty(), "No java files were generated.");
    result.forEach(file -> Assertions.assertTrue(this.doParseFile(file)));
  }

  protected boolean doParseFile(@NotNull File file) {
    Preconditions.checkNotNull(file);
    try {
      return this.getJavaParser().parse(file).isSuccessful();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    }
  }
}