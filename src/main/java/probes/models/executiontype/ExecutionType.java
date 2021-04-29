package probes.models.executiontype;

import probes.models.common.Command;
import probes.models.common.TypeName;
import probes.models.Task;

@TypeName(name = "executiontype", displayName = "Execution Type", description = "Execution Type")
public interface ExecutionType {

    int process(Command cmd, Task task) throws Exception;

}
