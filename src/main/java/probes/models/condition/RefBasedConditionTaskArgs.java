package probes.models.condition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import probes.models.Task;
import probes.models.TaskArgs;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;
import probes.models.executiontype.ExecutionType;
import probes.models.nameresolver.NameResolver;
import probes.util.DefLoader;

@Data
@Log4j
@TypeName(name = "refexpr", displayName = "Reference Expression", description = "condition expression which refers some pre-built conditions")
public class RefBasedConditionTaskArgs implements TaskArgs {
    
    @FieldDesc(displayName = "Reference expression", description = "List of fully qualified references to conditions defined in other modules or test suites")
    private String refs;

    @FieldDesc(displayName = "Input values", description = "Parameter values for the above conditions. The values can be concrete values or name of other parameter whose resolution is attempted again")
    private Map<String, String> inputs = new HashMap<>();

    @Override
    public int execute(Task task, ExecutionType type) throws Exception {
        boolean end = true;
        for (String ref : Arrays.asList(getRefs().split("\\s"))) {
            ConditionTask condition = DefLoader.load(ref, ConditionTask.class);
            log.debug("running refbasedcondition : " + condition.getFqName());
            int status = condition.run();
            log.debug("finished : " + condition.getFqName() + " status = " + status);
            end &= status == 0;
            if (!end)
                break;
        }
        return end ? 0 : 1;
    }

}
