// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Objects;

import ch.ethz.idsc.retina.dev.joystick.JoystickType;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class GenericXboxPadLcmServerModule extends AbstractModule {
  /** refresh period in [ms] for joystick events */
  public static final int PERIOD_MS = 20;
  // ---
  private JoystickLcmServer joystickLcmServer;

  @Override
  protected final void first() throws Exception {
    joystickLcmServer = new JoystickLcmServer(getJoystickType(), PERIOD_MS);
    joystickLcmServer.start();
  }

  @Override
  protected final void last() {
    if (Objects.nonNull(joystickLcmServer)) {
      joystickLcmServer.stop();
      joystickLcmServer = null;
    }
  }

  public JoystickType getJoystickType() {
    return JoystickType.GENERIC_XBOX_PAD;
  }
}
