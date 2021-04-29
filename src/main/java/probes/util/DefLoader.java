package probes.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import probes.models.*;
import probes.models.common.Config;
import probes.models.common.TypeName;
import probes.models.condition.ConditionTask;
import probes.models.executiontype.ExecutionType;
import probes.models.nameresolver.NameResolver;
import probes.models.variables.Variable;

import java.io.*;
import java.util.*;

public class DefLoader {

    private static Logger LOG = Logger.getLogger(DefLoader.class);

    private static Map<String, Object> cache = new HashMap<>();

    private static GsonBuilder gb = new GsonBuilder().setPrettyPrinting();

    private static Random R = new Random();

    private static String root = Config.getModPath();

    // this can be queried to get the directory from where the current object is getting loaded.
    private static String curLoadDir;
    
    static {

        Reflections reflections = new Reflections("probes", new SubTypesScanner());
        RuntimeTypeAdapterFactory<TaskArgs> rtafTA = RuntimeTypeAdapterFactory.of(TaskArgs.class, "type");
        reflections.getSubTypesOf(TaskArgs.class).forEach(ta -> {
            if (ta.getAnnotation(TypeName.class) != null) {
                LOG.debug("registering taskargs subtype: " + ta.getName());
                rtafTA.registerSubtype(ta, ta.getAnnotation(TypeName.class).name());
            }
        });
        gb.registerTypeAdapterFactory(rtafTA);

        RuntimeTypeAdapterFactory<Task> rtafT = RuntimeTypeAdapterFactory.of(Task.class, "type");
        reflections.getSubTypesOf(Task.class).forEach(ta -> {
            if (ta.getAnnotation(TypeName.class) != null) {
                LOG.debug("registering task subtype: " + ta.getName());
                rtafT.registerSubtype(ta, ta.getAnnotation(TypeName.class).name());
            }
        });
        gb.registerTypeAdapterFactory(rtafT);

        RuntimeTypeAdapterFactory<ExecutionType> rtafET = RuntimeTypeAdapterFactory.of(ExecutionType.class, "type");
        reflections.getSubTypesOf(ExecutionType.class).forEach(ta -> {
            if (ta.getAnnotation(TypeName.class) != null) {
                LOG.debug("registering exectype subtype: " + ta.getName());
                rtafET.registerSubtype(ta, ta.getAnnotation(TypeName.class).name());
            }
        });
        gb.registerTypeAdapterFactory(rtafET);

        RuntimeTypeAdapterFactory<NameResolver> rtafNR = RuntimeTypeAdapterFactory.of(NameResolver.class, "type");
        reflections.getSubTypesOf(NameResolver.class).forEach(ta -> {
            if (ta.getAnnotation(TypeName.class) != null) {
                LOG.debug("registering nameresolver subtype: " + ta.getName());
                rtafNR.registerSubtype(ta, ta.getAnnotation(TypeName.class).name());
            }
        });
        gb.registerTypeAdapterFactory(rtafNR);

        RuntimeTypeAdapterFactory<Variable> rtafV = RuntimeTypeAdapterFactory.of(Variable.class, "type");
        reflections.getSubTypesOf(Variable.class).forEach(ta -> {
            if (ta.getAnnotation(TypeName.class) != null) {
                LOG.debug("registering variables subtype: " + ta.getName());
                rtafV.registerSubtype(ta, ta.getAnnotation(TypeName.class).name());
            }
        });
        gb.registerTypeAdapterFactory(rtafV);

        gb.registerTypeAdapterFactory(PostConstructTypeAdapterFactory.getPostConstructTypeAdapterFactory());
    }

    private static String getModuleName(String name) {
        return name.substring(0, name.contains(".") ? name.lastIndexOf(".") : 0);
    }

    private static String getModuleFilePath(String name) {
        return FQCtoPath(name) + "/module.json";
    }

    private static String makeKlassFilePath(String name) {
        return FQCtoPath(name) + ".json";
    }

    private static String FQCtoPath(String name) {
        return root + "/" + name.replace(".", "/");
    }

    @SuppressWarnings("all")
    public synchronized static <T> T load(String name, Class<T> clazz) {

        if (cache.containsKey(name)) {
            return (T) cache.get(name);
        }

        // assumimg this is a fully qualified class name
        String fileName = makeKlassFilePath(name);
        LOG.debug("Loading " + fileName);

        Gson gson = gb.create();

        try {
            curLoadDir = new File(fileName).getParentFile().getAbsolutePath();
            T obj = gson.fromJson(new FileReader(fileName), clazz);
            cache.put(name, obj);
            return obj;
        } catch (FileNotFoundException fnfe) {
            LOG.debug("file not found exception for: " + fileName);
            try {
                // assuming this item is in a package
                final String pkgPath = getModuleFilePath(getModuleName(name));
                final String pkgName = getModuleName(name) + ".";

                LOG.debug("Loading " + pkgPath);
                curLoadDir = new File(pkgPath.replace(".", "/")).getParentFile().getAbsolutePath();

                ModuleDefn mod = gson.fromJson(new FileReader(pkgPath), ModuleDefn.class);

                mod.getConditions().forEach(c -> cache.put(pkgName + c.getName(), c));
                mod.getNameResolvers().forEach(c -> cache.put(pkgName + c.getName(), c));
                mod.getTests().forEach(c -> cache.put(pkgName + c.getName(), c));
                mod.getVariables().forEach(c -> cache.put(pkgName + c.getName(), c));

                if (cache.containsKey(name) && clazz.isAssignableFrom(cache.get(name).getClass())) {
                    return (T) cache.get(name);
                }

                throw new RuntimeException(name + " is not a module or a unit test or a condition or a provider");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(name + " is neither a module, unit test, condition or a provider");
            }
        }
    }

    public static ModuleDefn loadMod(String name) throws Exception {
        return gb.create().fromJson(new FileReader(getModuleFilePath(name)), ModuleDefn.class);
    }

    public static String register(Object o) {
        String tn = "tmp" + o.getClass().getCanonicalName() + "-" + R.nextInt();
        cache.put(tn, o);
        return tn;
    }

    public static void unregister(String name) {
        cache.remove(name);
    }

    public static String getLoadDir() {
        return curLoadDir;
    }

    public static void main(String[] args) {
        load("testinput.tmodule.file-exists", ConditionTask.class);
        load("infa.ihs.ssl_test_suite", TestSuiteDefn.class);
    }

}
