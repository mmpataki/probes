package probes.models.noop;

import java.util.HashMap;
import java.util.Map;

import probes.models.Task;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;
import probes.models.nameresolver.NameResolver;
import probes.util.DefLoader;

@TypeName(name = "unitask", displayName = "Uni Task", description = "single task which acts like a placeholder")
public class UniTask extends Task {

    @Override
    public int _run() throws Exception {
        return getArgs().execute(this, getExecutionType());
    }

}
