package probes.models.executiontype;

import probes.models.common.Command;
import probes.models.Task;
import probes.models.common.TypeName;

@TypeName(name = "local", displayName = "Local execution", description = "A command which executes on the local machine")
public class LocalExecutionType implements ExecutionType {

    @Override
    public int process(Command cmd, Task task) throws Exception {
        return CommandExecutor.builder().cmd(cmd).task(task).build().execute();
    }
    
}
