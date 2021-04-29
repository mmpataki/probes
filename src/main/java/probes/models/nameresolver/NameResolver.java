package probes.models.nameresolver;

import java.util.*;
import lombok.Data;
import probes.models.NamedItem;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;

import lombok.extern.log4j.Log4j;

@Log4j
@Data
@TypeName(name = "nameresolver", displayName = "Name Resolver", description = "a component which resolved unknown parameters")
public abstract class NameResolver extends NamedItem {

    String type;

    List<NameResolver> nameResolvers = new ArrayList<>();

    public String resolve(String name) throws Exception {
        log.debug("refnr = " + this + " for = " + name);
        String val = _resolve(name);
        if(val == null)
            return val;
        if(val.startsWith("$")) {
            String key = val;
            log.debug("me=" + getClass() + " " + this + " res=" + nameResolvers);
            for (NameResolver nr : nameResolvers) {
                val = nr.resolve(key);
                if(val != null)
                    return val;
            }
            return key;
        } else {
            return val;
        }
    }

    public abstract String _resolve(String name) throws Exception;

}
