package de.monticore.lang.montiarc.tag.drawing;

public class ConnectorLayoutSymbol extends DrawableSymbol {
  public static final ConnectorLayoutKind KIND = ConnectorLayoutKind.INSTANCE;

  public ConnectorLayoutSymbol(int id, int x, int y, int endX, int endY,
      int midX, int midY) {
    super(KIND, id, x, y);
    addValues(endX, endY, midX, midY);
  }

  protected ConnectorLayoutSymbol(ConnectorLayoutKind kind, int id, int x, int y, int endX, int endY,
      int midX, int midY) {
    super(kind, id, x, y);
    addValues(endX, endY, midX, midY);
  }

  public int getEndX() {
    return getValue(3);
  }

  public int getEndY() {
    return getValue(4);
  }

  public int getMidX() {
    return getValue(5);
  }

  public int getMidY() {
    return getValue(6);
  }

  @Override
  public String toString() {
    return String.format(
        "connectorLayout { id=%d, start=(%d, %d), end=(%d, %d), mid=(%d, %d) }",
        getId(), getX(), getY(), getEndX(), getEndY(),
        getMidX(), getMidY());
  }

  public static class ConnectorLayoutKind extends DrawableKind {
    public static final ConnectorLayoutKind INSTANCE = new ConnectorLayoutKind();

    protected ConnectorLayoutKind() {
    }
  }
}
