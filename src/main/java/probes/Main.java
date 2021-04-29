package probes;

import lombok.extern.log4j.Log4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Log4j
public class Main {

    private Map<String, Action> actionsMap = new HashMap<String, Action>() {
        private static final long serialVersionUID = 2709086944614992185L;
        {
            put("list", new ListAction());
            put("run", new RunAction());
            put("dui", new DumpUiConfig());
        }
    };

    /**
     *
     * @param args : action actionArg1 actionArg2 ... possible actions: list run
     *
     */
    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    public void run(String args[]) throws Exception {

        if (args.length < 1) {
            log.error("Usage: {} action actionArg1 actionArg2 actionArg3 ...");
            System.exit(1);
        }

        String action = args[0];
        String actionArgs[] = Arrays.copyOfRange(args, 1, args.length);

        actionsMap.get(action).perform(actionArgs);

    }

}
