// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.offline.api.LogFile;
import ch.ethz.idsc.gokart.offline.tab.OfflineVectorTable;
import ch.ethz.idsc.gokart.offline.tab.OfflineVectorTables;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

enum ProduceReport {
  ;
  public static void of(LogFile logFile) throws IOException {
    File file = DatahakiLogFileLocator.file(logFile);
    // ---
    List<OfflineVectorTable> list = Arrays.asList( //
        OfflineVectorTables.linmotGet(), //
        OfflineVectorTables.linmotPut(), //
        OfflineVectorTables.miscGet(), //
        OfflineVectorTables.miscPut(), //
        OfflineVectorTables.steerGet(), //
        OfflineVectorTables.steerPut(), //
        OfflineVectorTables.rimoGet(), //
        OfflineVectorTables.rimoPut() //
    );
    long tic = System.currentTimeMillis();
    OfflineLogPlayer.process(file, list);
    System.out.println(System.currentTimeMillis() - tic);
    // ---
    File dir = UserHome.file("export/" + logFile.getTitle());
    dir.mkdirs();
    for (OfflineVectorTable offlineTableSupplier : list)
      Export.of( //
          new File(dir, offlineTableSupplier.channel() + ".csv"), //
          offlineTableSupplier.getTable().map(CsvFormat.strict()));
  }

  // 5448, 5268
  public static void main(String[] args) throws IOException {
    // File folder = UserHome.file("gokart/linmot/20180412T164740");
    // GokartLogInterface gli = GokartLogAdapter.of(folder);
    // ---
    of(GokartLogFile._20180614T142228_6a2f62c6);
  }
}
