package probes.models.condition;

import lombok.extern.log4j.Log4j;
import probes.models.Task;
import probes.models.common.TypeName;

@Log4j
@TypeName(name = "condition", displayName = "Condition", description = "A task which evaluates a condition")
public class ConditionTask extends Task {

    @Override
    public int _run() throws Exception {
        int status = getArgs().execute(this, getExecutionType());
        log.info("condition evaluation : " + (status == 0 ? "succeeded" : "failed") + " code: " + status);
        if(status == 0) {
            for (Task st : getSubTasks()) {
                st.run();
            }
            return 0;
        }
        return status;
    }
    
}
