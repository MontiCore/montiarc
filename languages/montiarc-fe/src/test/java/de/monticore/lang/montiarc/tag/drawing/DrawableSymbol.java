package de.monticore.lang.montiarc.tag.drawing;

import de.monticore.lang.montiarc.tagging._symboltable.TagKind;
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbol;

/**
 * Super class which defines basic properties for all drawable objects
 * Derived classes: Canvas, Component, Port and Connector
 */
public class DrawableSymbol extends TagSymbol {
  public static final DrawableKind KIND = DrawableKind.INSTANCE;

  // unique id moved to IdGenerator

  public DrawableSymbol(int id, int x, int y) {
    super(KIND, id, x, y);
    // always store all values in the super class
    // b/c this class handles equals and hashCode for you
  }

  protected DrawableSymbol(DrawableKind kind, int id, int x, int y) {
    super(kind, id, x, y);
  }

  public int getId() {
    return getValue(0);
  }

  public int getX() {
    return getValue(1);
  }

  public int getY() {
    return getValue(2);
  }

  @Override
  public String toString() {
    return String.format("drawable { id=%s, x=%s, y=%s }",  getId(), getX(), getY());
  }

  public static class DrawableKind extends TagKind {
    public static final DrawableKind INSTANCE = new DrawableKind();

    protected DrawableKind() {
    }
  }
}
