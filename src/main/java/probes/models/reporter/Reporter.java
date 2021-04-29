package probes.models.reporter;

import probes.models.test.TestTask;

public interface Reporter {
    
    public void report(TestTask test, int status);

}
