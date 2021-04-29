package probes.models.executiontype;

import probes.models.common.Command;
import probes.models.Task;
import probes.util.DefLoader;
import probes.models.nameresolver.NameResolver;
import probes.models.variables.Variable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

import lombok.*;
import lombok.Builder.Default;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
@AllArgsConstructor
@Builder
public class CommandExecutor {

    int status;
    Command cmd;
    Task task;

    @Default
    Random R = new Random();

    OutputStream in;
    InputStream out, err;

    public CommandExecutor(Command cmd, Task task) {
        this.cmd = cmd;
        this.task = task;
    }

    public int execute() throws Exception {

        String fName = "script_" + R.nextInt() + ".sh";

        try {
            // pre-hook
            preProcess();

            writeScript(fName);

            ProcessBuilder pb = new ProcessBuilder(getCommand(fName));
            try {
                pb.redirectErrorStream(cmd.isMergeStreams());
                if (cmd.isInheritIo()) {
                    pb.inheritIO();
                }

                log.info("starting process : " + pb);

                Process proc = pb.start();

                in = proc.getOutputStream();
                out = proc.getInputStream();
                err = proc.getErrorStream();

                status = proc.waitFor();

                log.info("process finished with code = " + status);
            } catch (Exception e) {
                log.error("Failed to execute process: " + pb, e);
                return -1;
            }

            // post-hook
            postProcess();
        } finally {
            Files.deleteIfExists(Paths.get(fName));
        }

        return status;
    }

    public void postProcess() {
    }

    public List<String> getCommand(String scriptFileName) {
        return Arrays.asList("bash", scriptFileName);
    }

    private void writeScript(String fName) throws Exception {

        try {

            FileWriter fw = new FileWriter(fName);

            if(cmd.getCwd() != null) {
                fw.append("cd ").append(cmd.getCwd()).append("\n");
            }

            // write env
            resolveEnv(cmd.getEnvironment()).forEach((k, v) -> {
                try {
                    fw.append("export ").append(k).append('=').append(v).append('\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // $MYDIR must be set to the path where this task is defined.
            fw.append("export MYDIR=").append(task.getDefinitionDir()).append("\n");

            // write command and arguments
            fw.append(cmd.getCmd()).append(" ");
            resolveArgs(cmd.getArguments()).forEach(x -> {
                try {
                    fw.append(x).append(" ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void preProcess() {
    }

    private Map<String, String> resolveEnv(List<String> environment) throws Exception {
        Map<String, String> ret = new HashMap<>();
        for (String env : environment) {
            String key, value = null;
            if (env.startsWith("$")) {
                value = task.resolve(env);
                key = env.substring(1);
            } else {
                key = env.substring(0, env.indexOf('='));
                value = env.substring(env.indexOf('=') + 2);
            }
            ret.put(key, value);
        }
        return ret;
    }

    private List<String> resolveArgs(List<String> arguments) throws Exception {
        List<String> ret = new ArrayList<>();
        for (String arg : arguments) {
            arg = task.resolve(arg);
            ret.add(arg);
        }
        return ret;
    }
}
