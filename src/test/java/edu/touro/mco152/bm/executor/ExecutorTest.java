package edu.touro.mco152.bm.executor;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.TestObserver;
import edu.touro.mco152.bm.builder.CommandValueBuilder;
import edu.touro.mco152.bm.builder.IBuilder;
import edu.touro.mco152.bm.client.BenchmarkClient;
import edu.touro.mco152.bm.client.BenchmarkClientTest;
import edu.touro.mco152.bm.command.BMCommandCenter;
import edu.touro.mco152.bm.command.BMWriteActionCommandCenter;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import edu.touro.mco152.bm.ui.NonSwingBenchmark;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExecutorTest {

    @BeforeAll
    /**
     * Bruteforce setup of static classes/fields to allow DiskWorker to run.
     *
     * @author lcmcohen
     */
    static void setupDefaultAsPerProperties() {
        /// Do the minimum of what  App.init() would do to allow to run.
        Gui.mainFrame = new MainFrame();
        App.p = new Properties();
        App.loadConfig();
        System.out.println(App.getConfigString());
        Gui.progressBar = Gui.mainFrame.getProgressBar(); //must be set or get Nullptr

        // configure the embedded DB in .jDiskMark
        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        // code from startBenchmark
        //4. create data dir reference
        App.dataDir = new File(App.locationDir.getAbsolutePath() + File.separator + App.DATADIRNAME);

        //5. remove existing test data if exist
        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        }
        else
        {
            App.dataDir.mkdirs(); // create data dir if not already present
        }
    }

    @Test
    public void test_execute(){
        setupDefaultAsPerProperties();

        IBuilder builder = new CommandValueBuilder(new NonSwingBenchmark()).numOfMarks(BenchmarkClient.numOfMarks)
                .numOfBlocks(BenchmarkClient.numOfBlocks).blockSizeKb(BenchmarkClient.blockSizeKb)
                .blockSequence(BenchmarkClient.blockSequence);

        BMCommandCenter command = new BMWriteActionCommandCenter(builder.getRequest());
        TestExecutor executor = new TestExecutor(command);
        assertTrue(executor.execute());
        assertTrue(executor.wasExecuted);
    }

}
