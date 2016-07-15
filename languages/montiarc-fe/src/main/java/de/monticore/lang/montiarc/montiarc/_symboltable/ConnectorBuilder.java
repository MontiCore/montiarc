package de.monticore.lang.montiarc.montiarc._symboltable;

import java.util.Optional;

import de.se_rwth.commons.logging.Log;

/**
 * Created by Michael von Wenckstern on 23.05.2016.
 */
public class ConnectorBuilder {
  protected Optional<String> source = Optional.empty();
  protected Optional<String> target = Optional.empty();

  public static ConnectorSymbol clone(ConnectorSymbol con) {
    return new ConnectorBuilder().setSource(con.getSource()).
        setTarget(con.getTarget()).build();
  }

  public ConnectorBuilder setSource(String source) {
    this.source = Optional.of(source);
    return this;
  }

  public ConnectorBuilder setTarget(String target) {
    this.target = Optional.of(target);
    return this;
  }

  @Deprecated
  public ConnectorSymbol build() {
    if (source.isPresent() && target.isPresent()) {
      ConnectorSymbol con = new ConnectorSymbol(this.target.get());
      con.setSource(this.source.get());
      con.setTarget(this.target.get());
      return con;
    }
    Log.error("not all parameters have been set before to build the connector symbol");
    throw new Error("not all parameters have been set before to build the connector symbol");
  }
}
