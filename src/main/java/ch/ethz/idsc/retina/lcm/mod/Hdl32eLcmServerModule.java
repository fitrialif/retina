// code by jph
package ch.ethz.idsc.retina.lcm.mod;

import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;

public class Hdl32eLcmServerModule extends VelodyneLcmServerModule {
  public Hdl32eLcmServerModule() {
    super(VelodyneModel.HDL32E, "center");
  }
}
