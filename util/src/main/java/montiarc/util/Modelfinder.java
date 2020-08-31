/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import java.io.File;
import java.util.*;

import org.apache.commons.io.FileUtils;

/**
 * Class that finds all models with a given file extension in a modelpath.
 *
 */
public class Modelfinder {
  /**
   * Finds all models with a certain file ending in the given model path
   * 
   * @param modelPath
   * @param fileExtension
   * @return List of all found models
   */
  public static List<String> getModelsInModelPath(File modelPath, String fileExtension) {
    List<String> models = new ArrayList<String>();
    String[] extension = { fileExtension };
    Collection<File> files = FileUtils.listFiles(modelPath, extension, true);
    for (File f : files) {
      String model = getDotSeperatedFQNModelName(modelPath.getPath(), f.getPath(), fileExtension);
      System.out.println("[Modelfinder] found model: " + model);
      models.add(model);
    }
    return models;
  }

  /**
   * Finds all files with a certain file ending in the given model paths (recursively)
   */
  public static Set<File> getModelFiles(String fileExtension, File... modelPaths) {
    Set<File> modelFiles = new HashSet<>();
    for (File file : modelPaths) {
      Collection<File> models = FileUtils
          .listFiles(file, new String[] { fileExtension }, true);
      modelFiles.addAll(models);
    }
    return modelFiles;
  }
  
  protected static String getDotSeperatedFQNModelName(String FQNModelPath, String FQNFilePath, String fileExtension) {
    if (FQNFilePath.contains(FQNModelPath)) {
      String fqnModelName = FQNFilePath.substring(FQNModelPath.length() + 1);
      fqnModelName = fqnModelName.replace("." + fileExtension, "");
      if (fqnModelName.contains("\\")) {
        fqnModelName = fqnModelName.replaceAll("\\\\", ".");
      }
      else if (fqnModelName.contains("/")) {
        fqnModelName = fqnModelName.replaceAll("/", ".");
      }
      
      return fqnModelName;
    }
    return FQNFilePath;
  }
}
