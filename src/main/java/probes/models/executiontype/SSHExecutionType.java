package probes.models.executiontype;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lombok.extern.log4j.Log4j;
import probes.models.common.Command;
import probes.models.common.FieldDesc;
import probes.models.Task;
import probes.models.nameresolver.*;
import probes.models.common.TypeName;
import probes.util.DefLoader;

/**
 * 
 * ssh based execution, requires password less ssh for the user on the required host.
 * 
 */

@TypeName(name = "ssh", displayName = "SSH based execution", description = "Execution which happens on remote nodes. Requires password less SSH to the remote node")
@Log4j
public class SSHExecutionType implements ExecutionType {

    @FieldDesc(displayName = "User Name", description = "User who runs the programs on remote node")
    String user;

    @FieldDesc(displayName = "Hostname", description = "Host name to run this command on")
    String hostname;

    @Override
    public int process(Command cmd, Task task) throws Exception {
        
        user = resolve(task, user);
        hostname = resolve(task, hostname);

        CommandExecutor ce = new CommandExecutor(cmd, task) {

            Random R = new Random();
            String newDir = "/tmp" + R.nextInt();
            String uAndH = String.format("%s@%s", user, hostname);

            @Override
            public void preProcess() {
                cmd.getArtifacts().forEach(this::copyArtifact);
            }

            public void copyArtifact(String localPath) {                
                try {
                    runShell(String.format("ssh %s mkdir -p %s", uAndH, newDir));
                    runShell(String.format("scp -pr %f %s:%s", localPath, uAndH, newDir));
                } catch (InterruptedException | IOException e) {
                    log.error("couldn't copy artifact " + localPath, e);
                }
            }

            private int runShell(String shellCommand) throws InterruptedException, IOException {
                return new ProcessBuilder("bash", "-c", shellCommand).inheritIO().start().waitFor();
            }

            @Override
            public List<String> getCommand(String scriptFileName) {
                return Arrays.asList(
                    "bash",
                    "-c",
                    String.format("cat %s | ssh %s@%s", scriptFileName, user, hostname)
                );
            }
        };

        return ce.execute();
    }

    private String resolve(Task task, String str) throws Exception {
        String val = task.resolve(str);
        if(val == null) {
            throw new Exception(str + " couldn't be resolved");
        }
        return val;
    }
}
