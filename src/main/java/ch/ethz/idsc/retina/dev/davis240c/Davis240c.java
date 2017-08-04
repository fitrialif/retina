// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

import ch.ethz.idsc.retina.dev.api.ApsReference;
import ch.ethz.idsc.retina.dev.api.DvsReference;

public enum Davis240c implements DvsReference, ApsReference {
  INSTANCE;
  // ---
  public static final int WIDTH = 240;
  static final int HEIGHT = 180;
  private static final int LAST_X = WIDTH - 1;
  private static final int LAST_Y = HEIGHT - 1;
  private static final int ADC_MAX = 1023;

  @Override
  public DvsDavisEvent encodeDvs(int time, int x, int y, int i) {
    return new DvsDavisEvent(time, LAST_X - x, LAST_Y - y, i);
  }

  @Override
  public ApsDavisEvent encodeAps(int time, int x, int y, int adc) {
    return new ApsDavisEvent(time, x, LAST_Y - y, ADC_MAX - adc);
  }
}