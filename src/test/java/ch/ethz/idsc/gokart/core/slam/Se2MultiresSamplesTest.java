// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.util.Arrays;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.qty.Degree;
import junit.framework.TestCase;

public class Se2MultiresSamplesTest extends TestCase {
  public void testSimple() {
    Se2MultiresGrids se2MultiresGrids = new Se2MultiresGrids( //
        RealScalar.of(0.03), //
        RealScalar.of(2 * 3.14 / 180), //
        1, 3);
    Se2Grid se2Grid = se2MultiresGrids.grid(2);
    assertEquals(se2Grid.gridPoints().size(), 27);
    assertEquals(Dimensions.of(se2Grid.gridPoints().get(12).matrix()), Arrays.asList(3, 3));
    assertEquals(se2MultiresGrids.grids(), 3);
  }

  public void testFan2() {
    Se2MultiresGrids se2MultiresGrids = new Se2MultiresGrids( //
        RealScalar.of(0.03), //
        RealScalar.of(2 * 3.14 / 180), //
        2, 3);
    Se2Grid se2Grid = se2MultiresGrids.grid(2);
    assertEquals(se2Grid.gridPoints().size(), 125);
    assertEquals(Dimensions.of(se2Grid.gridPoints().get(124).matrix()), Arrays.asList(3, 3));
    assertEquals(se2MultiresGrids.grids(), 3);
  }

  public void testPixelSpace() {
    Se2MultiresGrids se2MultiresGrids = new Se2MultiresGrids(RealScalar.of(1), Degree.of(4), 1, 4);
    Se2Grid se2Grid = se2MultiresGrids.grid(3);
    assertEquals(se2Grid.gridPoints().size(), 27);
    assertEquals(Dimensions.of(se2Grid.gridPoints().get(26).matrix()), Arrays.asList(3, 3));
    assertEquals(se2MultiresGrids.grids(), 4);
  }
}
