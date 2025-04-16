/* (c) https://github.com/MontiCore/monticore */
package parser;

/*
 * Parsable Model covering refinement syntax.
 */
component RefinementSyntax {

  component A refines B { }
  component A refines B, C { }
  component A refines B<int, float> {}
  component A refines B<int>, C<float> {}

  component A refines a.b.c.D, a.b.c.d.E {}
  component A refines B<int>, a.b.c.d.E<float> {}

  component A refines a.B<int>(12), a.b.C<double>(2.0) {}
  component A refines a.B<int>(12),
                      a.b.C<double>(2.0) {}
  component A refines B(12), C {}

  component A
    extends B<int>(13), C<double>(2.2)
    refines a.D<int>(12), a.E<double> { }
}
