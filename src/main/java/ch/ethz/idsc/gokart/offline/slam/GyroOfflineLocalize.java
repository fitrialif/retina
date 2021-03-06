// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.List;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.Se2MultiresGrids;
import ch.ethz.idsc.gokart.core.slam.SlamDunk;
import ch.ethz.idsc.gokart.core.slam.SlamResult;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.gui.top.ViewLcmFrame;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.N;

/** the test matches 3 consecutive lidar scans to the dubendorf hangar map
 * the matching qualities are 51255, 43605, 44115 */
public class GyroOfflineLocalize extends OfflineLocalize {
  private static final Scalar LIDAR_RATE = Quantity.of(20, "s^-1");
  private static final int MIN_POINTS = LocalizationConfig.GLOBAL.min_points.number().intValue();
  private static final Se2MultiresGrids SE2MULTIRESGRIDS = LocalizationConfig.GLOBAL.createSe2MultiresGrids();
  /** 3x3 transformation matrix of lidar to center of rear axle */
  private final Tensor lidar = SensorsConfig.GLOBAL.vlp16Gokart();
  private final ScatterImage scatterImage;

  /** @param model */
  public GyroOfflineLocalize(BufferedImage map_image, Tensor model, ScatterImage scatterImage) {
    super(map_image, model);
    this.scatterImage = scatterImage;
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    Tensor points = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    Scalar rate = getGyroAndReset().divide(LIDAR_RATE);
    // System.out.println("rate=" + rate);
    List<Tensor> list = LocalizationConfig.GLOBAL.getUniformResample() //
        .apply(points).getPointsSpin(rate);
    Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
    int sum = scattered.length(); // usually around 430
    if (MIN_POINTS < sum) {
      GeometricLayer geometricLayer = GeometricLayer.of(ViewLcmFrame.MODEL2PIXEL_INITIAL);
      Tensor rotate = Se2Utils.toSE2Matrix(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, rate));
      model = model.dot(rotate);
      geometricLayer.pushMatrix(model);
      geometricLayer.pushMatrix(lidar);
      Stopwatch stopwatch = Stopwatch.started();
      SlamResult slamResult = SlamDunk.of(SE2MULTIRESGRIDS, geometricLayer, scattered, slamScore);
      double duration = stopwatch.display_seconds(); // typical is 0.03
      Tensor pre_delta = slamResult.getTransform();
      Tensor poseDelta = lidar.dot(pre_delta).dot(Inverse.of(lidar));
      // Tensor dstate = Se2Utils.fromSE2Matrix(poseDelta);
      model = model.dot(poseDelta); // advance gokart
      Scalar ratio = N.DOUBLE.apply(slamResult.getMatchRatio());
      appendRow(ratio, sum, duration);
      scatterImage.render(model.dot(lidar), scattered);
    } else {
      System.err.println("few points " + sum);
      skip();
    }
  }
}