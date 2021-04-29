package probes.models;

import probes.models.executiontype.ExecutionType;

public interface TaskArgs {
    
    //TODO: rename this to eval/process
    public int execute(Task task, ExecutionType type) throws Exception;

}
