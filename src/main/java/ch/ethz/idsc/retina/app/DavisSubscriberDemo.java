// code by jph
package ch.ethz.idsc.retina.app;

import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.AccumulatedEventsImage;
import ch.ethz.idsc.retina.davis.app.DavisDefaultDisplay;
import ch.ethz.idsc.retina.davis.data.DavisApsDatagramDecoder;
import ch.ethz.idsc.retina.davis.data.DavisDvsDatagramDecoder;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

// TODO rename
class DavisSubscriberDemo {
  private final LCM lcm = LCM.getSingleton();
  final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  final DavisApsDatagramDecoder davisApsDatagramDecoder = new DavisApsDatagramDecoder();

  public DavisSubscriberDemo() {
    lcm.subscribe(DavisDvsBlockPublisher.CHANNEL, new LCMSubscriber() {
      @Override
      public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
        try {
          BinaryBlob dvsBlockLcm = new BinaryBlob(ins);
          davisDvsDatagramDecoder.decode(ByteBuffer.wrap(dvsBlockLcm.data));
          // System.out.println("ok " + dvsBlockLcm.currentTimeMillis);
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    });
    lcm.subscribe(DavisApsBlockPublisher.CHANNEL, new LCMSubscriber() {
      @Override
      public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
        try {
          BinaryBlob apsBlockLcm = new BinaryBlob(ins);
          davisApsDatagramDecoder.decode(ByteBuffer.wrap(apsBlockLcm.data));
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    });
  }

  public static void main(String[] args) throws Exception {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    DavisSubscriberDemo dvsBlockLcmReceiver = new DavisSubscriberDemo();
    DavisDefaultDisplay davisImageDisplay = new DavisDefaultDisplay(davisDevice);
    // handle dvs
    AccumulatedEventsImage accumulatedEventsImage = new AccumulatedEventsImage(davisDevice, 10000);
    dvsBlockLcmReceiver.davisDvsDatagramDecoder.addListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(davisImageDisplay);
    // handle aps
    dvsBlockLcmReceiver.davisApsDatagramDecoder.addListener(davisImageDisplay);
    // Thread.sleep(10000);
  }
}
