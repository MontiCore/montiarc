/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

// Should compile, by allowing connectors of compatible types
component A_Composition {

  BooleanIn bool2bool;
  CharIn c2c;
  ByteIn byte2byte;
  ShortIn byte2s, s2s;
  IntIn byte2i, s2i, c2i, i2i;
  LongIn byte2l, s2l, c2l, i2l, l2l;
  FloatIn byte2f, s2f, c2f, i2f, l2f, f2f;
  DoubleIn byte2d, s2d, c2d, i2d, l2d, f2d, d2d;

  BooleanOut boolO [p -> bool2bool.p;];

  CharOut co [
    p -> c2c.p;
    p -> c2i.p;
    p -> c2l.p;
    p -> c2f.p;
    p -> c2d.p;
  ];


  ByteOut byteO [
    p -> byte2byte.p;
    p -> byte2s.p;
    p -> byte2i.p;
    p -> byte2l.p;
    p -> byte2f.p;
    p -> byte2d.p;
  ];

  ShortOut sO [
    p -> s2s.p;
    p -> s2i.p;
    p -> s2l.p;
    p -> s2f.p;
    p -> s2d.p;
  ];

  IntOut iO [
    p -> i2i.p;
    p -> i2l.p;
    p -> i2f.p;
    p -> i2d.p;
  ];

  LongOut lO [
    p -> l2l.p;
    p -> l2f.p;
    p -> l2d.p;
  ];

  FloatOut fO [
    p -> f2f.p;
    p -> f2d.p;
  ];

  DoubleOut dO [
    p -> d2d.p;
  ];
}
