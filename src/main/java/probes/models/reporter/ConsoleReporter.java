package probes.models.reporter;

import lombok.extern.log4j.Log4j;
import probes.models.test.TestTask;

@Log4j
public class ConsoleReporter implements Reporter {

    @Override
    public void report(TestTask test, int status) {
        System.out.println("  test: " + test.getName());
        if(test.getDescription() != null)
            System.out.println("  desc: " + test.getDescription());
        System.out.println("status: " + (status == 0 ? "succeeded" : "failed"));
        if(status != 0) {
            System.out.println("mitigation steps: \n\t" + test.getMitigationSteps());
        }
    }

}
