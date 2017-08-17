package montiarc._symboltable;

import java.util.Optional;

import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

/**
 * Created by Michael von Wenckstern on 23.05.2016.
 */
public class PortBuilder {
  protected Optional<Boolean> incoming = Optional.empty();
  protected Optional<String> name = Optional.empty();
  protected Optional<JTypeReference> typeReference = Optional.empty();

  public static PortSymbol clone(PortSymbol port) {
    return new PortBuilder().setName(port.getName()).setDirection(port.isIncoming())
        .setTypeReference(port.getTypeReference()).build();
  }

  public PortBuilder setDirection(boolean incoming) {
    this.incoming = Optional.of(Boolean.valueOf(incoming));
    return this;
  }

  public PortBuilder setName(String name) {
    this.name = Optional.of(name);
    return this;
  }

  public PortBuilder setTypeReference(JTypeReference typeReference) {
    this.typeReference = Optional.of(typeReference);
    return this;
  }

  public PortSymbol build() {
    if (name.isPresent() && incoming.isPresent() && typeReference.isPresent()) {
      PortSymbol p = new PortSymbol(this.name.get());
      p.setDirection(this.incoming.get());
      p.setTypeReference(this.typeReference.get());
      return p;
    }
    Log.error("not all parameters have been set before to build the port symbol");
    throw new Error("not all parameters have been set before to build the port symbol");
  }
}
