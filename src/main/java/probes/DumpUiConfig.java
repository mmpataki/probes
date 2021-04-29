package probes;

import java.lang.reflect.*;
import java.util.*;

import com.google.gson.GsonBuilder;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import probes.models.ModuleDefn;
import probes.models.TestSuiteDefn;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;

@Log4j
public class DumpUiConfig extends Action {

    @Data
    @Builder
    public static class TypeInfo {
        boolean singleValued;

        /* set for multivalued */
        List<String> typeNames;
    }

    @Data
    @Builder
    public static class PField {
        String name;
        String displayName;
        String description;
        TypeInfo typeInfo;
    }

    @Data
    @Builder
    public static class PClass {
        String name;
        String displayName;
        String description;
        Map<String, PField> fields;
    }

    Map<String, PClass> types = new HashMap<>();
    Reflections ref = new Reflections("probes", new SubTypesScanner());

    private void eatSchema(Class<?> C) {

        if (!C.isAnnotationPresent(TypeName.class))
            return;

        TypeName tn = C.getAnnotation(TypeName.class);

        if (types.containsKey(tn.name()))
            return;

        Map<String, PField> fields = new LinkedHashMap<>();
        PClass clazz = PClass.builder().name(tn.name()).displayName(tn.displayName()).description(tn.description()).fields(fields).build();

        types.put(clazz.getName(), clazz);

        eatFields(C, fields);
    }

    private void eatFields(Class<?> C, Map<String, PField> fields) {
        if(!C.getCanonicalName().startsWith("probes"))
            return;
        eatFields(C.getSuperclass(), fields);
        for (Field f : C.getDeclaredFields()) {

            if (!f.isAnnotationPresent(FieldDesc.class))
                continue;

            FieldDesc ti = f.getAnnotation(FieldDesc.class);

            PField ele = PField.builder().name(f.getName()).displayName(ti.displayName())
                    .description(ti.description()).typeInfo(getTypeInfo(f)).build();

            fields.put(ele.getName(), ele);
        }
    }

    private TypeInfo getTypeInfo(Field f) {
        Class<?> typ = f.getType();
        if (typ.isPrimitive() || String.class.isAssignableFrom(typ) || Number.class.isAssignableFrom(typ)) {
            return TypeInfo.builder().singleValued(true).typeNames(Arrays.asList(typ.getName())).build();
        }
        if (typ.isArray()) {
            return TypeInfo.builder().singleValued(false).typeNames(getTypeNames(typ.getComponentType())).build();
        }
        if (Collection.class.isAssignableFrom(typ)) {
            return TypeInfo.builder().singleValued(false).typeNames(
                    getTypeNames((Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0]))
                    .build();
        }
        return TypeInfo.builder().singleValued(true).typeNames(getTypeNames(typ)).build();
    }

    private List<String> getTypeNames(Class typ) {
        if (typ.isInterface() || Modifier.isAbstract(typ.getModifiers())) {
            Set<Class> subTypes = ref.getSubTypesOf(typ);

            List<String> ret = new ArrayList<>();
            for (Class subTyp : subTypes) {
                if (subTyp.isAnnotationPresent(TypeName.class))
                    ret.add(((TypeName) subTyp.getAnnotation(TypeName.class)).name());
                eatSchema(subTyp);
            }
            if (!ret.isEmpty())
                return ret;
        }
        if(!typ.isInterface() & !Modifier.isAbstract(typ.getModifiers())) {
            eatSchema(typ);
        }
        return Collections.singletonList(
                typ.isAnnotationPresent(TypeName.class) ? ((TypeName) typ.getAnnotation(TypeName.class)).name()
                        : typ.getName());
    }

    @Override
    public void run(String... args) throws Exception {
        eatSchema(TestSuiteDefn.class);
        eatSchema(ModuleDefn.class);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(types));
    }

    public static void main(String[] args) throws Exception {
        new DumpUiConfig().run();
    }

}
