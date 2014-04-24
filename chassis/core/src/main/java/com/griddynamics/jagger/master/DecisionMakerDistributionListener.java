package com.griddynamics.jagger.master;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.model.RootNode;
import com.griddynamics.jagger.dbapi.model.SummaryNode;
import com.griddynamics.jagger.engine.e1.ProviderUtil;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import com.griddynamics.jagger.engine.e1.services.JaggerPlace;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.sessioncomparation.DecisionMakerInfo;
import com.griddynamics.jagger.engine.e1.sessioncomparation.TestGroupDecisionMakerListener;
import com.griddynamics.jagger.master.configuration.Task;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 2/7/14
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class DecisionMakerDistributionListener implements DistributionListener {
    private NodeContext nodeContext;
    private DatabaseService databaseService;

    public DecisionMakerDistributionListener(NodeContext nodeContext, DatabaseService databaseService) {
        this.nodeContext = nodeContext;
        this.databaseService = databaseService;
    }

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        //do nothing
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        //??? why executed twice when 2 test groups

        if (task instanceof CompositeTask) {
            TestGroupDecisionMakerListener decisionMakerListener = TestGroupDecisionMakerListener.Composer.compose(ProviderUtil.provideElements(((CompositeTask) task).getDecisionMakerListeners(),
                    sessionId,
                    taskId,
                    nodeContext,
                    JaggerPlace.TEST_GROUP_DECISION_MAKER_LISTENER));

            DataService dataService = new DefaultDataService(nodeContext);

            SessionEntity sessionEntity = dataService.getSession(sessionId);
            System.out.println(sessionEntity);

            //??? decisionMakerListener

//            //???
            RootNode rootNode = databaseService.getControlTreeForSessions(new HashSet<String>(Arrays.asList(sessionId)));
            SummaryNode summaryNode = rootNode.getSummaryNode();
//
//
//            CompositeTask compositeTask = (CompositeTask) task;
//            List<CompositableTask> children = new ArrayList<CompositableTask>();
//            for (CompositableTask compositableTask : compositeTask.getAttendant()) {
//                if (compositableTask instanceof WorkloadTask) {
//                    children.add(compositableTask);
//                }
//            }
//            for (CompositableTask compositableTask : compositeTask.getLeading()) {
//                if (compositableTask instanceof WorkloadTask) {
//                    children.add(compositableTask);
//                }
//            }


            DecisionMakerInfo decisionMakerInfo = new DecisionMakerInfo();
            decisionMakerListener.onDecisionMaking(decisionMakerInfo);
        }
    }
}
