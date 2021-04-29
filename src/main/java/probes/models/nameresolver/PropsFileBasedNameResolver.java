package probes.models.nameresolver;

import java.io.*;
import java.util.Properties;

import probes.models.common.TypeName;

@TypeName(name = "propsnr", displayName = "Properties file based name resolver", description = "a resolver which reads a property file to resolves the names")
public class PropsFileBasedNameResolver extends NameResolver {

    Properties props = new Properties();

    String fileName;

    @Override
    public String _resolve(String name) throws Exception {
        try {
            if (props.isEmpty()) {
                props.load(new FileInputStream(fileName));
            }
            props.get(name);
        } catch (IOException e) {
            return null;
        }
        return null;
    }
}
