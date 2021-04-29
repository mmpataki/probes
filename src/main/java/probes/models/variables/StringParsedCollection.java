package probes.models.variables;

import java.util.Arrays;
import java.util.Collections;

import lombok.Data;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;
import probes.models.nameresolver.NameResolver;
import probes.util.DefLoader;

@Data
@TypeName(name = "stringparsedcollection", displayName = "String parsed collection", description = "a collection generated based on a string separated by some separator (default ,)")
public class StringParsedCollection extends Collection {

    @FieldDesc(displayName = "String value", description = "Values separated using separator")
    String strValue = "";

    @FieldDesc(displayName = "Separator", description = "Value separator (default: ',')")
    String separator = ",";

    @Override
    public Iterable<?> getIterable() throws Exception {
        if (!strValue.startsWith("$"))
            return Arrays.asList(strValue.split(separator));
        for (NameResolver nr : getNameResolvers()) {
            String val = nr.resolve(strValue.substring(1));
            if (val != null) {
                return Arrays.asList(val.split(separator));
            }
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Object getValue() throws Exception {
        return getIterable();
    }

}
