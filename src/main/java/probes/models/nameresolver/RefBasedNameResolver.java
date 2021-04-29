package probes.models.nameresolver;

import java.util.HashMap;
import java.util.Map;

import probes.models.Task;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;
import probes.util.DefLoader;

import lombok.Data;
import lombok.extern.log4j.Log4j;

/**
 * name resolver which uses a map to resolve the names first and then tries
 * given task's resolvers to resolve a name
 */
@Data
@Log4j
@TypeName(name = "refnr", displayName = "Reference Name resolver", description = "Reference based name resolver")
public class RefBasedNameResolver extends NameResolver {

    @FieldDesc(displayName = "Ref", description = "Reference to a pre-defined name-resolver")
    String ref;

    @FieldDesc(displayName = "Inputs", description = "A key value map as hint to referred name resolver")
    Map<String, String> inputs = new HashMap<>();

    @Override
    public String _resolve(String name) throws Exception {
        NameResolver nr = DefLoader.load(ref, NameResolver.class);
        try {
            nr.getNameResolvers().add(0, new MapBasedNameResolver(inputs));
            return nr.resolve(name);
        } finally {
            nr.getNameResolvers().remove(0);
        }
    }

}
