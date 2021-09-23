package org.drools;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.demo20210928.client.App;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

public class LocalTest {

    @Test
    public void testContainment() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kieContainer = ks.getKieClasspathContainer();
        KieSession ksession = kieContainer.newKieSession();

        BatchExecutionCommand batch = App.batchCommands();
        ExecutionResults batchResult = ksession.execute(batch);

        QueryResults results = (QueryResults) batchResult.getValue("qid");
        Iterator<QueryResultsRow> i = results.iterator();
        while (i.hasNext()) {
            QueryResultsRow row = i.next();
            System.out.println(Stream.of(results.getIdentifiers()).map(fid -> fid + ": "+row.get(fid)).collect(Collectors.joining(", ")));
            // System.out.println(row.get("x") + " contains " + row.get("y") );
        }
    }
}
