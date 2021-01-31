/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.github.javaparser.JavaParser;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

public class GeneratorTest {

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
  public void quickGeneratorTest(@NotNull String modelPath) throws IOException {
    //Given
    POJOGeneratorTool tool = new POJOGeneratorTool(Paths.get(TEST_TARGET_PATH), Paths.get(TEST_JAVA_PATH));

    //When
    tool.generateAllTypesInPath(Paths.get(TEST_RESOURCE_PATH, modelPath));

    //Then
    Assertions.assertAll(() -> FileUtils.listFiles(Paths.get(TEST_TARGET_PATH).toFile(), new String[]{"java"}, true)
      .forEach(file -> Assertions.assertTrue(this.doParseFile(file))));
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