// (c) https://github.com/MontiCore/monticore
package montiarc.util;

import de.se_rwth.commons.logging.Log;

import java.io.File;

public class DirectoryUtil {

  /**
   * Compares the two paths and returns the common path. The common path is the
   * basedir.
   *
   * @param modelPath
   * @param targetPath
   * @return
   */
  public static String getBasedirFromModelAndTargetPath(String modelPath, String targetPath) {
    String basedir = "";
    StringBuilder sb = new StringBuilder();
    String seperator = File.separator;
    int lastFolderIndex = 0;
    for (int i = 0; i < modelPath.length(); i++) {
      // Assuming a seperator is always length 1
      if (seperator.length() != 1) {
        Log.error("0x???? File seperator should be a single char. Use a less strange system");
      }
      else if (modelPath.charAt(i) == seperator.charAt(0)) {
        lastFolderIndex = i;
      }

      if (modelPath.charAt(i) == targetPath.charAt(i)) {
        sb.append(modelPath.charAt(i));
      }
      else {
        // basedir includes the seperator
        basedir = sb.substring(0, lastFolderIndex + 1);
        break;
      }
    }
    return basedir;
  }
}
