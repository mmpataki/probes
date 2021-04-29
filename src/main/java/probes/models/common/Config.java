package probes.models.common;

public class Config {
    
    public static String getModPath() {
        return System.getProperty("modPath", "./modules");
    }

}
