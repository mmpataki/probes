package probes.models.variables;

import java.util.ArrayList;
import java.util.List;

import probes.models.common.FieldDesc;
import probes.models.common.TypeName;

@TypeName(name = "listbasedcollection", displayName = "List based collection", description = "a collection which is based on a hardcoded list")
public class ListBasedCollection extends Collection {
    
    @FieldDesc(displayName = "Value", description = "A JSON list")
    List<Object> value = new ArrayList<>();

    @Override
    public Object getValue() throws Exception {
        return value;
    }

    @Override
    public Iterable<Object> getIterable() throws Exception {
        return value;
    }

}
