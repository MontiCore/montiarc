/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.DelegatingConfigurationContributor;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class POJOConfiguration {

  public static final String CONFIGURATION_PROPERTY = "_configuration";

  public static final String DEFAULT_OUTPUT_DIRECTORY = "out";
  public static final String OUT = "out";
  public static final String OUT_SHORT = "o";

  public static final String[] MODELPATH = {"modelPath", "mp", "model", "models"};
  protected final Configuration configuration;

  public static POJOConfiguration withConfiguration(@NotNull Configuration configuration) {
    Preconditions.checkNotNull(configuration);
    return new POJOConfiguration(configuration);
  }

  protected POJOConfiguration(@NotNull Configuration internal) {
    this.configuration = ConfigurationContributorChainBuilder.newChain()
      .add(DelegatingConfigurationContributor.with(Preconditions.checkNotNull(internal)))
      .build();
  }

  /**
   * {@see de.se_rwth.commons.configuration.Configuration#getAllValues()} this also adds the custom special values
   * required for the {@link POJOGeneratorScript#generate(List, File) generator}
   */
  public Map<String, Object> getValueMap() {
    Map<String, Object> values = new HashMap<>(this.configuration.getAllValues());
    normalize(values, MODELPATH);
    values.put(OUT, getOut());
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
      this.configuration.getAsString(OUT)
        .orElseGet(() -> this.configuration.getAsString(OUT_SHORT)
          .orElse(DEFAULT_OUTPUT_DIRECTORY))
    );
  }
}