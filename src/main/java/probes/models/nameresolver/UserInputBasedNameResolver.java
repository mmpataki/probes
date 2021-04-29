package probes.models.nameresolver;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import probes.models.common.TypeName;

@Data
@Log4j
@TypeName(name = "inputnr", displayName = "User input based name resolver", description = "a resolver which prompts the user to get the value")
public class UserInputBasedNameResolver extends NameResolver {

    Properties cache = new Properties();
    static Scanner sc = new Scanner(System.in);
    String lastFile = null;

    public UserInputBasedNameResolver() {
        for (String f : new File(".").list()) {
            if(f.startsWith("uin.")) {
                long ts = Long.parseLong(f.split("\\.")[1]);
                if((System.currentTimeMillis() - ts) < 10 * 60 * 1000) {//10 mins
                    try {
                        cache.load(new FileInputStream(f));
                        log.info("loaded from cache [" + f + "] " + cache);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    break;
                } else {
                    try {
                        new File(f).delete();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public String _resolve(String name) throws Exception {
        if(cache.containsKey(name))
            return (String)cache.get(name);
        System.out.printf("Enter value for %s:\n", name);
        String val = (String)sc.nextLine();
        cache.put(name, val);
        try {
            if(lastFile != null) {
                new File(lastFile).delete();
            }
            lastFile = "uin." + System.currentTimeMillis();
            cache.save(new FileOutputStream(lastFile), "");
        } catch(Exception e){
            e.printStackTrace();
        }
        return val;
    }

}
