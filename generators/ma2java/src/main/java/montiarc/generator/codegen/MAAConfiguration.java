/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.configuration.Configuration;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MAAConfiguration {
  public static final String CONFIGURATION_PROPERTY = "_configuration";
  public static final String DEFAULT_OUTPUT_DIRECTORY = "out";

  public static final String OUTPUT_PATH = "out";
  public static final String OUTPUT_SHORT = "o";

  public static final String[] MODEL = {"modelPath", "mp", "model", "models"};
  public static final String[] CODE = {"handwrittenCode", "hwc"};

  private final Configuration configuration;

  public static MAAConfiguration withConfiguration(@NotNull Configuration configuration) {
    Preconditions.checkNotNull(configuration);
    return new MAAConfiguration(configuration);
  }

  protected MAAConfiguration(@NotNull Configuration configuration) {
    this.configuration = Preconditions.checkNotNull(configuration);
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValues() this also adds the custom special values
   * required for the {@link MAAGeneratorScript#generate(List, File, List) generator}
   */
  public Map<String, Object> getValueMap() {
    Map<String, Object> values = new HashMap<>(this.configuration.getAllValues());
    normalize(values, MODEL);
    normalize(values, CODE);
    values.put(OUTPUT_PATH, getOut());
    return values;
  }

  /**
   * collects all values aggregated under the given keys into a list that is stored as a value of the first key
   *
   * @param map  map to modify
   * @param keys array with at least one entry
   */
  public void normalize(@NotNull Map<String, Object> map, @NotNull String... keys) {
    Preconditions.checkNotNull(map);
    Preconditions.checkNotNull(keys);
    Preconditions.checkArgument(keys.length != 0);
    map.put(keys[0], Arrays.stream(keys)
      .map(configuration::getAsStrings)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .flatMap(Collection::stream)
      .map(Paths::get)
      .map(Path::toFile)
      .collect(Collectors.toList()));
  }

  /**
   * Getter for the output directory stored in this configuration. A fallback default is "out".
   *
   * @return output directory file
   */
  public File getOut() {
    return new File(
      this.configuration.getAsString(OUTPUT_PATH)
        .orElseGet(() -> this.configuration.getAsString(OUTPUT_SHORT)
          .orElse(DEFAULT_OUTPUT_DIRECTORY))
    );
  }
}