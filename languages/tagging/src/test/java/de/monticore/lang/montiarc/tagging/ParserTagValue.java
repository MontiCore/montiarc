package de.monticore.lang.montiarc.tagging;

import de.monticore.lang.montiarc.tagging._ast.ASTTaggingUnit;
import de.monticore.lang.montiarc.tagging._parser.TaggingParser;
import org.junit.Test;

/**
 * Created by MichaelvonWenckstern on 13.06.2016.
 */
public class ParserTagValue {

//  @Test
//  public void testUnit() throws Exception {
//    TagValueParser parser = new TagValueParser();
//    assertTrue(parser.parseString_UnitTagValue("150 mW").isPresent());
//  }
//
//  @Test
//  public void testString() throws Exception {
//    TagValueParser parser = new TagValueParser();
//    assertTrue(parser.parseString_StringTagValue("\"MvW\"").isPresent());
//  }
//
//  @Test
//  public void testBoolean() throws Exception {
//    TagValueParser parser = new TagValueParser();
//    assertTrue(parser.parseString_BooleanTagValue("true").isPresent());
//  }

//  @Test
//  public void testTags() throws Exception {
//    TagsParser parser = new TagsParser();
//    ASTTags tags = parser.parse("C:\\Users\\MichaelvonWenckstern\\Documents\\MontiArc4\\01.code\\tagging\\src\\test\\resources\\windTurbine\\PowerConsumptionTags.tag").get();
//    System.out.println(tags.getTags().size());
//    tags.getTags().stream().filter(t -> t.getTagValue().isPresent())
//        .forEachOrdered(t -> System.out.println(t.getTagValue().get()));
////    System.out.println(tags.getTags().get(0).getTagValue().length());
//  }

  @Test
  public void testTagschema2() throws Exception {
    TaggingParser parser = new TaggingParser();
    ASTTaggingUnit tags = parser.parse("C:\\Users\\MichaelvonWenckstern\\Documents\\MontiArc4\\01.code\\tagging\\src\\test\\resources\\windTurbine\\PowerConsumption.tag").get();
    tags.getTagBody().getTags().forEach(t -> t.getTagElements().forEach(e -> System.out.println(e.getName() + ": " + e.getTagValue().orElse(""))));
//    System.out.println(tags.getTags().size());
    //    System.out.println(tags.getTags().get(0).getTagValue().length());
  }
}
