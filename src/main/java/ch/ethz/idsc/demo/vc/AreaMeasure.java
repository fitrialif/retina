// code by vc
package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/** approximates measure of {@link Area} by uniformly sampling points and counting containment */
public enum AreaMeasure {
  ;
  /** resolution */
  private static final int RES = 300;

  /** @param area
   * @return */
  public static double of(Area area) {
    int count = 0;
    Rectangle2D bounds2d = area.getBounds2D();
    double x = bounds2d.getX();
    double y = bounds2d.getY();
    double width = bounds2d.getWidth();
    double height = bounds2d.getHeight();
    for (int i = 0; i < RES; i++) {
      for (int j = 0; j < RES; j++) {
        Point2D point = new Point2D.Double(x + i * width / RES, y + j * height / RES);
        if (area.contains(point))
          ++count;
      }
    }
    return height * width * count / (RES * RES);
  }
}
