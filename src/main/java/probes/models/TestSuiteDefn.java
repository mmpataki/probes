package probes.models;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;

import java.util.List;

@Data
@Log4j
@TypeName(name = "testsuite", displayName = "Test suite", description = "A collection of tests")
public class TestSuiteDefn extends NamedItem {

    @FieldDesc(displayName = "Tasks", description = "List of tasks which will be executed as part of this test suite")
    List<Task> tasks;

    public void run() throws Exception {
        for (Task task : tasks) {
            log.info("running task" + task.getFqName());
            task.run();
            log.info("task finished : " + task.getFqName());
        }
    }

}
