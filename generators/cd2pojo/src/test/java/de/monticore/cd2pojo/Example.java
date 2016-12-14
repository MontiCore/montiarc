package de.monticore.cd2pojo;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class Example {
  @Test
  public void test() {
    Path outDir = Paths.get("out/");
    Path modelPath = Paths.get("src/test/resources/models");
    String modelName = "domain.Domain";
    new POJOGenerator(outDir, modelPath, modelName).generate();
    String targetPackage = "some.custom._package";
    new POJOGenerator(outDir, modelPath, modelName, targetPackage).generate();
  }
}
