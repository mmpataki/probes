package probes.models.test;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import probes.models.Task;
import probes.models.TaskArgs;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;
import probes.models.executiontype.ExecutionType;
import probes.models.nameresolver.NameResolver;
import probes.util.DefLoader;

@Data
@TypeName(name = "ref", displayName = "Test reference", description = "test arguments which specifies the tests predefined tests")
public class RefBasedTestTaskArgs implements TaskArgs {
    
    @FieldDesc(displayName = "Reference", description = "Fully qualified name of the test")
    String ref;

    @Override
    public int execute(Task task, ExecutionType type) throws Exception {
        TestTask test = DefLoader.load(ref, TestTask.class);
        int status = test.run();
        return status;
    }
}
