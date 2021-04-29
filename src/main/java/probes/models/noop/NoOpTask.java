package probes.models.noop;

import java.util.HashMap;
import java.util.Map;

import probes.models.Task;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;
import probes.models.nameresolver.NameResolver;
import probes.util.DefLoader;

@TypeName(name = "nooptask", displayName = "No-Op Task", description = "task which aggregates other tasks, Useful in providing common inputs")
public class NoOpTask extends Task {

    @Override
    public int _run() throws Exception {
        for (Task st : getSubTasks()) {
            try {
                for (NameResolver nrName : getNameResolvers()) {
                    st.getNameResolvers().add(0, nrName);
                }
                st.run();
            } finally {
                for (int i = 0; i < getNameResolvers().size(); i++) {
                    st.getNameResolvers().remove(0);
                }
            }
        }
        return 0;
    }

}
