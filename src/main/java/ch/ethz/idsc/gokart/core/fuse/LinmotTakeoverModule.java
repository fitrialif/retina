// code by rvmoss and jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.sys.SafetyCritical;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** module detects when human presses the break while the software
 * is controlling the break
 * 
 * module has to be stopped and restarted once fuse is blown */
@SafetyCritical
public final class LinmotTakeoverModule extends EmergencyModule<LinmotPutEvent> implements LinmotGetListener {
  /** in order for fuse to blow, the position discrepancy
   * has to be maintained for 0.05[s] */
  private static final long DURATION_MS = 50;
  /** position discrepancy threshold
   * anything below threshold is expected during normal operation */
  private static final double THRESHOLD_POS_DELTA = 20000;
  // ---
  private final Watchdog watchdog = new Watchdog(DURATION_MS * 1E-3);
  private boolean isBlown = false;

  @Override // from AbstractModule
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addGetListener(this);
    LinmotSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    LinmotSocket.INSTANCE.removeGetListener(this);
    LinmotSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    if (linmotGetEvent.getPositionDiscrepancyRaw() <= THRESHOLD_POS_DELTA) // abs(int) not used
      watchdog.pacify(); // <- at nominal rate the watchdog is notified every 4[ms]
  }

  /***************************************************/
  @Override // from LinmotPutProvider
  public Optional<LinmotPutEvent> putEvent() {
    isBlown |= watchdog.isBlown();
    return Optional.ofNullable(isBlown ? LinmotPutOperation.INSTANCE.offMode() : null); // deactivate break
  }
}
