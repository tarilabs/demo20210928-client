package org.drools.demo20210928.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.demo20210928.model.Containment;
import org.drools.demo20210928.model.Thing;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

public class App {
    private static final String URL = "http://localhost:8080/kie-server/services/rest/server";
    private static final String USER = "krisv";
    private static final String PASSWORD = "krisv";
    private static final MarshallingFormat FORMAT = MarshallingFormat.XSTREAM;
    private KieServicesConfiguration conf;
    private KieServicesClient kieServicesClient;
    private static final String containerId = "demo20210928-rules_1.0.0-SNAPSHOT";

    public void initialize() {
        conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
        conf.setMarshallingFormat(FORMAT);
        conf.addExtraClasses(new HashSet<>(Arrays.asList(Thing.class, Containment.class)));
        kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
    }

    private void demo() {
        initialize();
        RuleServicesClient ruleClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
        ServiceResponse<ExecutionResults> executeResponse = ruleClient.executeCommandsWithResults(containerId, batchCommands());
        System.out.println("ServiceResponse.type: " + executeResponse.getType());
        ExecutionResults serverResult = executeResponse.getResult();
        for (String serverResultId : serverResult.getIdentifiers()) {
            System.out.println("ServiceResponse.result."+serverResultId+": "+serverResult.getValue(serverResultId));
        }
        QueryResults results = (QueryResults) serverResult.getValue("qid");
        Iterator<QueryResultsRow> i = results.iterator();
        while (i.hasNext()) {
            QueryResultsRow row = i.next();
            System.out.println(Stream.of(results.getIdentifiers()).map(fid -> fid + ": "+row.get(fid)).collect(Collectors.joining(", ")));
            // System.out.println(row.get("x") + " contains " + row.get("y") );
        }
    }

    public static BatchExecutionCommand batchCommands() {
        Thing house   = new Thing("house", 1000);
        Thing office  = new Thing("office", 200);
        Thing desk    = new Thing("desk",   100);
        Thing library = new Thing("library",100);
        Thing box     = new Thing("box",     10);
        Thing key     = new Thing("key",      1);
        
        KieCommands kieCommands = KieServices.Factory.get().getCommands();
        List<Command<?>> commandList = new ArrayList<Command<?>>();
        commandList.add(kieCommands.newInsert(house));
        commandList.add(kieCommands.newInsert(office));
        commandList.add(kieCommands.newInsert(desk));
        commandList.add(kieCommands.newInsert(library));
        commandList.add(kieCommands.newInsert(box));
        commandList.add(kieCommands.newInsert(key));

        commandList.add(kieCommands.newFireAllRules());

        // commandList.add(kieCommands.newQuery("qid", "findContainment", new Object[] { Variable.v, key }));
        // commandList.add(kieCommands.newQuery("qid", "isContained", new Object[] { key }));
        commandList.add(kieCommands.newQuery("qid", "allContainments"));

        BatchExecutionCommand batch = kieCommands.newBatchExecution(commandList);
        return batch;
    }

    public static void main(String[] args) {
        App a = new App();
        a.demo();
    }
}
