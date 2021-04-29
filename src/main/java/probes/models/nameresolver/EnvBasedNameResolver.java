package probes.models.nameresolver;

import probes.models.common.TypeName;
import lombok.Data;

@Data
@TypeName(name = "envnr", displayName = "Env based Name resolver", description = "a resolver which finds the values based on the environment")
public class EnvBasedNameResolver extends NameResolver {

    @Override
    public String _resolve(String name) throws Exception {
        return System.getenv(name);
    }
    
}
