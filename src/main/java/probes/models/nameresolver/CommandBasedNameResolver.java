package probes.models.nameresolver;

import java.util.Properties;
import java.util.stream.Collectors;

import lombok.Data;
import probes.models.Task;
import probes.models.common.Command;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;
import probes.models.executiontype.CommandExecutor;
import lombok.extern.log4j.Log4j;

@Log4j
@Data
@TypeName(name = "cmdnr", displayName = "Command based Name resolver", description = "a resolver which runs a command to find the values")
public class CommandBasedNameResolver extends NameResolver {

    @FieldDesc(displayName = "cmd", description = "Command to run to compute the value of a parameter")
    Command cmd;

    Properties props = new Properties();

    @Override
    public String _resolve(String name) throws Exception {
        Task tsk = new Task(){ 
            {
                getNameResolvers().addAll(0, CommandBasedNameResolver.this.getNameResolvers());
            }
            @Override
            public String resolve(String key) throws Exception {
                return super.resolve(key);
            }
            @Override
            public int _run() throws Exception {
                return 0;
            }
        };
        if (props.isEmpty()) {
            CommandExecutor ce = CommandExecutor.builder().cmd(cmd).task(tsk).build();
            int status = ce.execute();
            if (status != 0) {
                log.warn("Couldn't resolve " + name + "as command failed to execute");
                return null;
            }
            props.load(ce.getOut());
        }
        return props.getProperty(name);
    }

}
