package probes.models.common;

import java.util.*;

import lombok.Data;
import probes.models.Task;
import probes.models.TaskArgs;
import probes.models.executiontype.ExecutionType;

@Data
@TypeName(name = "cmd", displayName = "Command", description = "A command task to execute binaries")
public class Command implements TaskArgs {

    @FieldDesc(displayName = "Command", description = "Path to command which needs to be executed")
    String cmd;

    @FieldDesc(displayName = "Current working directory", description = "Current working directory of this command")
    String cwd;

    @FieldDesc(displayName = "File artifacts", description = "File artifacts required to execute this command, useful in SSH execution type")
    List<String> artifacts = new ArrayList<>();

    @FieldDesc(displayName = "Arguments", description = "List of command line arguments")
    List<String> arguments = new ArrayList<>();

    @FieldDesc(displayName = "Environment variables", description = "List of environment variables. If they need to be resolved, provide $envName; if you know the value, provide envName=envValue")
    List<String> environment = new ArrayList<>();

    @FieldDesc(displayName = "Merge std streams?", description = "Merge stdout and stderr?")
    boolean mergeStreams = true;

    @FieldDesc(displayName = "Inherit parent IO", description = "Inherit IO of the parent")
    boolean inheritIo = false;

    @Override
    public int execute(Task task, ExecutionType type) throws Exception {
        return type.process(this, task);
    }

}
