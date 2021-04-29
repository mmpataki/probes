package probes.models;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import probes.models.common.*;
import probes.models.executiontype.*;
import probes.models.nameresolver.*;
import probes.models.variables.*;
import probes.util.*;

@Data
@Log4j
@TypeName(name = "task", displayName = "Task", description = "A task item")
public abstract class Task extends NamedItem {

    @FieldDesc(displayName = "Arguments", description = "Task arguments")
    private TaskArgs args;

    @FieldDesc(displayName = "Sub Tasks", description = "Sub tasks")
    private List<Task> subTasks = new ArrayList<>();

    @FieldDesc(displayName = "Execution type", description = "Execution type")
    private ExecutionType executionType = new LocalExecutionType();

    @FieldDesc(displayName = "Name Resolver IDs", description = "List of name resolvers which can help in resolving the parameters")
    private List<NameResolver> nameResolvers = new ArrayList<>();

    private static final NameResolver envnr = new EnvBasedNameResolver();
    private static final NameResolver uinnr = new UserInputBasedNameResolver();

    private Pattern envMatchPattern = Pattern.compile("\\$\\{([A-Za-z0-9_]+)\\}");

    public String resolve(String key) throws Exception {

        if(key.equals("${MYDIR}"))
            return key;

        log.info("Bef resolve(" + key + ") = " + getNameResolvers());
        String value = null;

        Matcher m = envMatchPattern.matcher(key);
        if(m.find()) {
            if((m.group(1).length() + 3) < key.length()) {
                StringBuffer sb = new StringBuffer();
                do {
                    String val = resolve(String.format("${%s}", m.group(1)));
                    if(val.startsWith("$")) {
                        val = "\\" + val;
                    }
                    m.appendReplacement(sb, val);
                } while(m.find());
                value = m.appendTail(sb).toString();
                return value;
            } else {
                String tmpKey = key.substring(2, key.length() - 1);
                if (tmpKey.startsWith("var:")) {
                    String tVname = tmpKey.replace("var:", "");
                    try {
                        Variable v = DefLoader.load(tVname, Variable.class);
                        if (v != null) {
                            v.getNameResolvers().addAll(0, getNameResolvers());
                            value = v.getValue().toString();
                            for (int i = 0; i < getNameResolvers().size(); i++) {
                                v.getNameResolvers().remove(0);
                            }
                        }
                    } catch (Exception e) {
                        log.debug("trying loading variable " + tmpKey + " but failed", e);
                    }
                }
                if (value == null) {
                    for (NameResolver provider : getNameResolvers()) {
                        value = provider.resolve(tmpKey);
                        if (value != null) {
                            break;
                        }
                    }
                }
                if(value == null) {
                    value = envnr.resolve(tmpKey);
                }
                if(value == null) {
                    value = uinnr.resolve(tmpKey);
                }
                return value.startsWith("$") ? resolve(value) : value;
            }
        } else {
            return key;
        }
    }

    public int run() throws Exception {
        log.info("running task : " + getFqName());
        int status = _run();
        log.info("task finished: " + getFqName() + " status=" + status);
        return status;
    }

    public abstract int _run() throws Exception;

}