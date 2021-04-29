package probes;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import lombok.extern.log4j.Log4j;
import probes.models.ModuleDefn;
import probes.models.NamedItem;
import probes.models.TestSuiteDefn;
import probes.models.common.Config;
import probes.util.DefLoader;

@Log4j
public class ListAction extends Action {

    boolean verbose = false;

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            listAll();
        } else {
            if (args.length > 1) {
                verbose = args[1].equals("-v");
            }
            switch (args[0]) {
            case "ts":
                listTestSuites();
                return;
            case "mod":
                listModules();
                return;
            }
        }
    }

    interface DFSConsumer {
        void consume(File fileName, String path);
    }

    ListAction $(String s) {
        System.out.print(s);
        return this;
    }

    private void listModules() {
        dfs(new File(Config.getModPath()), "", (f, p) -> {
            if (f.isDirectory()) {
                return;
            }
            if (f.getName().equals("module.json")) {
                try {
                    ModuleDefn mod = DefLoader.loadMod(p);
                    $(p).$(mod.getDescription() == null ? "" : (" : " + mod.getDescription())).$("\n");

                    if(!verbose)
                        return;

                    $("\tconditions\n");
                    mod.getConditions().forEach(this::namedItemPrinter);

                    $("\tvariables\n");
                    mod.getVariables().forEach(this::namedItemPrinter);

                    $("\tname resolvers\n");
                    mod.getNameResolvers().forEach(this::namedItemPrinter);

                    $("\ttests\n");
                    mod.getTests().forEach(this::namedItemPrinter);

                } catch (Exception e) {
                    if (verbose) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void namedItemPrinter(NamedItem ni) {
        $("\t\t").$(ni.getName()).$(ni.getDescription() != null ? (" :: " + ni.getDescription()) : "").$("\n");
    }

    private void listTestSuites() {
        dfs(new File(Config.getModPath()), "", (f, p) -> {
            if(f.isDirectory() || !f.getName().endsWith(".json") || f.getName().equals("module.json"))
                return;
            TestSuiteDefn ts = null;
            try {
                ts = DefLoader.load(p + "." + f.getName().substring(0, f.getName().indexOf(".json")), TestSuiteDefn.class);
            } catch(RuntimeException re) {
                if(re.getMessage().contains("is neither a module"))
                    return;
                throw re;
            }
            $(p).$(".").$(ts.getName()).$(ts.getDescription() != null ? (" : " + ts.getDescription()) : "").$("\n");
        });
    }

    public void dfs(File f, String cp, DFSConsumer c) {
        Arrays.stream(f.listFiles()).forEach(sf -> {
            if (sf.isDirectory()) {
                dfs(sf, cp + (cp.isEmpty() ? "" : ".") + sf.getName(), c);
            } else if(sf.getName().endsWith(".json")) {
                c.consume(sf, cp);
            }
        });
    }

    private void listAll() {
        listModules();
        listTestSuites();
    }

    public static void main(String[] args) {
        new ListAction().run();
    }

}
