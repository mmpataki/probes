package probes.models;

import lombok.Data;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;
import probes.models.condition.ConditionTask;
import probes.models.test.TestTask;
import probes.models.variables.Variable;
import probes.models.nameresolver.*;

import java.util.LinkedList;
import java.util.List;

@Data
@TypeName(name = "module", displayName = "Module", description = "A collection of reusable variables, collections, tests nameresolvers and conditions")
public class ModuleDefn extends NamedItem {

    @FieldDesc(displayName = "Tests", description = "Re-usable tests")
    List<TestTask> tests = new LinkedList<>();

    @FieldDesc(displayName = "Conditions", description = "Re-usable conditions")
    List<ConditionTask> conditions = new LinkedList<>();

    @FieldDesc(displayName = "Name Resolvers", description = "Components to resolve unknown parameters")
    List<NameResolver> nameResolvers = new LinkedList<>();

    @FieldDesc(displayName = "Variables", description = "Re-usable variables")
    List<Variable> variables = new LinkedList<>();

}
