package probes.models.test;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import probes.models.Task;
import probes.models.common.FieldDesc;
import probes.models.common.TypeName;
import probes.models.reporter.ConsoleReporter;
import probes.models.reporter.Reporter;

@Data
@Log4j
@TypeName(name = "test", displayName = "Test task", description = "a test which asserts something")
public class TestTask extends Task {

    @FieldDesc(displayName = "Mitigation steps", description = "Steps to fix problem this test is detecting")
    String mitigationSteps;

    Reporter rep = new ConsoleReporter();

    @Override
    public int _run() throws Exception {
        log.info("running test : " + getFqName());
        int status;
        if(getArgs() == null) {
            boolean b = true;
            for (Task task : getSubTasks()) {
                int tmpStatus = task.run();
                if(task instanceof TestTask) {
                    b &= (tmpStatus == 0);
                }
            }
            status = b ? 0 : 1;
        } else {
            status = getArgs().execute(this, getExecutionType());
        }
        rep.report(this, status);
        return status;
    }
    
}
