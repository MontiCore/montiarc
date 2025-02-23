/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

// Should compile, by allowing connectors of compatible types
component A_Composition {

  In<boolean> bool2bool;
  In<char> c2c;
  In<byte> byte2byte;
  In<short> byte2s, s2s;
  In<int> byte2i, s2i, c2i, i2i;
  In<long> byte2l, s2l, c2l, i2l, l2l;
  In<float> byte2f, s2f, c2f, i2f, l2f, f2f;
  In<double> byte2d, s2d, c2d, i2d, l2d, f2d, d2d;

  Out<boolean> boolO [p -> bool2bool.p;];

  Out<char> co [
    p -> c2c.p;
    p -> c2i.p;
    p -> c2l.p;
    p -> c2f.p;
    p -> c2d.p;
  ];


  Out<byte> byteO [
    p -> byte2byte.p;
    p -> byte2s.p;
    p -> byte2i.p;
    p -> byte2l.p;
    p -> byte2f.p;
    p -> byte2d.p;
  ];

  Out<short> sO [
    p -> s2s.p;
    p -> s2i.p;
    p -> s2l.p;
    p -> s2f.p;
    p -> s2d.p;
  ];

  Out<int> iO [
    p -> i2i.p;
    p -> i2l.p;
    p -> i2f.p;
    p -> i2d.p;
  ];

  Out<long> lO [
    p -> l2l.p;
    p -> l2f.p;
    p -> l2d.p;
  ];

  Out<float> fO [
    p -> f2f.p;
    p -> f2d.p;
  ];

  Out<double> dO [
    p -> d2d.p;
  ];
}
