package probes.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import probes.models.common.FieldDesc;
import probes.util.DefLoader;

@AllArgsConstructor
@Getter
@Builder
public class NamedItem {
    
    @FieldDesc(displayName = "Name", description = "name of this entity (unique)")
    String name;

    @FieldDesc(displayName = "Description", description = "description about this entity")
    String description;

    String myDir;

    public NamedItem() {
        // this is a temporary hack to set the myDir
        myDir = DefLoader.getLoadDir();
    }

    public String getFqName() {
        return String.format("[%s :: %s]", getClass().getCanonicalName(), name);
    }

    public String getDefinitionDir() {
        return myDir;
    }

}
