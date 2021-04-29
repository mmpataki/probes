package probes.models.variables;

import lombok.Data;
import probes.models.common.TypeName;
import probes.models.nameresolver.NameResolver;
import probes.util.DefLoader;
import lombok.extern.log4j.Log4j;

@Log4j
@TypeName(name = "var", displayName = "Single valued variable", description = "a variable")
public class SingleValuedVariable extends Variable {

    String value;

    @Override
    public Object getValue() throws Exception {
        log.debug(value + "" + this);
        if(!value.startsWith("$"))
            return value;
        String vname = value.substring(1);
        for (NameResolver provider : getNameResolvers()) {
            String val = provider.resolve(vname);
            if (val != null) {
                return val;
            }
        }
        return null;
    }

}
