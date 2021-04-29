package probes.models.variables;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import probes.models.NamedItem;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;
import probes.models.nameresolver.NameResolver;

@Data
@TypeName(name = "variable", displayName = "Variable", description = "a variable")
public abstract class Variable extends NamedItem {
    
    @FieldDesc(displayName = "Name resolvers", description = "name resolvers to use to resolve this variable value (if this is also parameterized)")
    List<NameResolver> nameResolvers = new ArrayList<>();

    public abstract Object getValue() throws Exception;

}
