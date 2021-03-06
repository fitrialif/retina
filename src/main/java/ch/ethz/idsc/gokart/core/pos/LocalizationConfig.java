// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.io.Serializable;

import ch.ethz.idsc.gokart.core.slam.LidarGyroLocalization;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.core.slam.Se2MultiresGrids;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16TiltedPlanarEmulator;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.ParametricResample;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/**  */
public class LocalizationConfig implements Serializable {
  public static final LocalizationConfig GLOBAL = AppResources.load(new LocalizationConfig());
  /***************************************************/
  public Scalar gridShift = Quantity.of(0.5, SI.METER);
  public Scalar gridAngle = Quantity.of(1.5, NonSI.DEGREE_ANGLE);
  public Scalar gridFan = RealScalar.of(1);
  public Scalar gridLevels = RealScalar.of(4);
  /** positive integer 0, 1, 2, 4
   * smaller means better precision but larger memory footprint
   * value 1 is sufficient */
  public Scalar bitShift = RealScalar.of(0);
  /** inclination of rays to create cross section
   * a positive value means upwards */
  public Scalar horizon = Quantity.of(1, NonSI.DEGREE_ANGLE);
  /** minimum number of lidar points below which a matching of lidar with
   * static geometry will not be executed and localization will not update */
  public Scalar min_points = RealScalar.of(220);
  public Scalar threshold = RealScalar.of(33.0);
  public Scalar resampleDs = RealScalar.of(0.4);

  /***************************************************/
  /**
   * 
   */
  public Se2MultiresGrids createSe2MultiresGrids() {
    return new Se2MultiresGrids( //
        Magnitude.METER.apply(gridShift), //
        Magnitude.ONE.apply(gridAngle), //
        gridFan.number().intValue(), //
        gridLevels.number().intValue());
  }

  /** the VLP-16 is tilted by 0.04[rad] around the y-axis.
   * 
   * @return lidar spacial provider that approximates measurements
   * at the best approximation of given horizon level */
  public LidarSpacialProvider planarEmulatorVlp16() {
    SensorsConfig sensorsConfig = SensorsConfig.GLOBAL;
    int bits = bitShift.number().intValue();
    double angle_offset = sensorsConfig.vlp16_twist.number().doubleValue();
    double tiltY = sensorsConfig.vlp16_incline.number().doubleValue();
    double emulation_deg = Magnitude.DEGREE_ANGLE.apply(horizon).number().doubleValue();
    return new Vlp16TiltedPlanarEmulator(bits, angle_offset, tiltY, emulation_deg);
  }

  public ParametricResample getUniformResample() {
    return new ParametricResample(threshold, resampleDs);
  }

  /***************************************************/
  /** @return predefined map with static geometry for lidar based localization */
  public static PredefinedMap getPredefinedMap() {
    return PredefinedMap.DUBILAB_LOCALIZATION_20180610;
  }

  /** @return new instance of LidarGyroLocalization method */
  public static LidarGyroLocalization getLidarGyroLocalization() {
    return new LidarGyroLocalization(getPredefinedMap());
  }

  public static PredefinedMap getPredefinedMapObstacles() {
    return PredefinedMap.DUBILAB_OBSTACLES_20180610;
  }
}
