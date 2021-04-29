package probes;

import org.apache.log4j.Logger;

import probes.models.TestSuiteDefn;
import probes.util.DefLoader;

public class RunAction extends Action {

    Logger LOG = Logger.getLogger(RunAction.class);

    /**
     *
     * @param args : testSuite1 testSuite2 ...
     */
    @Override
    public void run(String ...args) throws Exception {
        for (String tSuite : args) {
            LOG.info("Running : " + tSuite);
            _run(tSuite);
            LOG.info("Finished " + tSuite);
        }
    }

    private void _run(String tSuite) throws Exception {
        TestSuiteDefn ts = DefLoader.load(tSuite, TestSuiteDefn.class);
        ts.run();
    }

    public static void main(String[] args) throws Exception {
        new RunAction().run("infa.eic.graph-corruption");
    }
}
