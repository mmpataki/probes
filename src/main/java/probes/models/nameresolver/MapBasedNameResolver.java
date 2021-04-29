package probes.models.nameresolver;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

import probes.models.Task;
import probes.util.DefLoader;

/**
 * name resolver which uses a map to resolve the names first and then tries
 * given task's resolvers to resolve a name
 */
@Data
public class MapBasedNameResolver extends NameResolver {

    private Map<String, String> inputs = new HashMap<>();

    public MapBasedNameResolver(Map<String, String> inputMap) {
        this.inputs = inputMap;
    }

    @Override
    public String _resolve(String name) throws Exception {
        return inputs.get(name);
    }

}
