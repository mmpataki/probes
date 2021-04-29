package probes.models.variables;

import probes.models.common.TypeName;

@TypeName(name = "collection", displayName = "Collection", description = "collection")
public abstract class Collection extends Variable {
    
    public abstract Iterable<?> getIterable() throws Exception;

}
