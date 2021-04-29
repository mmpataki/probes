package probes;

import org.apache.log4j.Logger;

public abstract class Action {

    Logger LOG = Logger.getLogger(Action.class);

    public abstract void run(String ...args) throws Exception;

    public void perform(String[] actionArgs) throws Exception {
        LOG.debug("Doing " + this.getClass().getSimpleName());
        run(actionArgs);
        LOG.debug("finished " + this.getClass().getSimpleName());
    }
}
