package probes.models.iterator;

import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.log4j.Log4j;
import probes.models.Task;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;
import probes.models.nameresolver.NameResolver;
import probes.models.variables.Collection;
import probes.util.DefLoader;

@Log4j
@TypeName(name = "iter", displayName = "Iteration Task", description = "task to iterate on a collection")
public class IteratorTask extends Task {

    @FieldDesc(displayName = "iterator variable name", description = "A variable name which hold the value for current iteration")
    String itvName;

    @FieldDesc(displayName = "Collection", description = "A collection to iterate on")
    String collection;

    @Override
    public int _run() throws Exception {
        final AtomicReference<String> curVal = new AtomicReference<>();
        NameResolver nr = new NameResolver(){
            @Override
            public String _resolve(String name) throws Exception {
                if(itvName.equals(name)) {
                    return curVal.get();
                }
                return null;
            }
        };

        Collection coll = DefLoader.load(collection, Collection.class);

        for (Object o : coll.getIterable()) {
            curVal.set(o.toString());

            for (Task t : getSubTasks()) {
                t.getNameResolvers().add(0, nr);
                t.run();
                t.getNameResolvers().remove(0);
            }
        }
        return 0;
    }
    
}
