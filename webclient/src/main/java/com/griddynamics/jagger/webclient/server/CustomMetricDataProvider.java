package com.griddynamics.jagger.webclient.server;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by kgribov on 3/5/14.
 */
public class CustomMetricDataProvider {

    public static Set<MetricNameDto> getMetricNames(EntityManager entityManager, List<TaskDataDto> tests, MetricDescriptionLoader metricDescriptionLoader){
        Set<Long> taskIds = new HashSet<Long>();
        for (TaskDataDto tdd : tests) {
            taskIds.addAll(tdd.getIds());
        }

        List<Object[]> parents = getParents(entityManager, taskIds);
        //???
        // un db session #35 testGroup is empty => groupIds empty => loadTestsMetricDescriptions fails
        List<Object[]> testGroups = getTestGroups(entityManager, taskIds);

        Multimap<Long, Long> testGroupMap = getTestsInTestGroup(parents, testGroups);

        Set<Long> groupIds = new HashSet<Long>(testGroups.size());
        for (Object[] testGroup : testGroups){
            groupIds.add(((BigInteger) testGroup[0]).longValue());
        }

        List<Object[]> metricDescriptions = metricDescriptionLoader.loadTestsMetricDescriptions(taskIds);
        List<Object[]> groupsMetricDescriptions = metricDescriptionLoader.loadTestsMetricDescriptions(groupIds);

        if (metricDescriptions.isEmpty() && groupsMetricDescriptions.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<MetricNameDto> metrics = new HashSet<MetricNameDto>(metricDescriptions.size()+groupsMetricDescriptions.size());

        // add test metric names
        for (Object[] mde : metricDescriptions) {
            for (TaskDataDto td : tests) {
                if (td.getIds().contains((Long) mde[2])) {
                    metrics.add(new MetricNameDto(td, (String)mde[0], (String)mde[1], MetricNameDto.Origin.METRIC));
                    break;
                }
            }
        }

        // add test-group metric names
        for (Object[] mde : groupsMetricDescriptions){
            for (TaskDataDto td : tests){
                Collection<Long> allTestsInGroup = testGroupMap.get((Long)mde[2]);
                if (containsAtLeastOne(td.getIds(), allTestsInGroup)){
                    metrics.add(new MetricNameDto(td, (String)mde[0], (String)mde[1], MetricNameDto.Origin.METRIC));
                }
            }
        }

        return metrics;
    }

    /**
     * @param taskIds ids of all tasks
     * @param metricId identifier of metric
     * @return list of object[] (value, sessionId, metricId, taskDataId)
     */
    public static List<Object[]> getMetricSummary(EntityManager entityManager, Set<Long> taskIds, Set<String> metricId, MetricSummaryLoader summaryLoader){
        List<Object[]> parents = getParents(entityManager, taskIds);
        List<Object[]> testGroups = getTestGroups(entityManager, taskIds);

        Multimap<Long, Long> testMap = getTestsInTestGroup(parents, testGroups);

        Set<Long> groupIds = new HashSet<Long>(testGroups.size());
        for (Object[] testGroup : testGroups){
            groupIds.add(((BigInteger) testGroup[0]).longValue());
        }

        List<Object[]> testsSummary = summaryLoader.loadMetricSummary(taskIds, metricId);
        List<Object[]> testGroupsSummary = summaryLoader.loadMetricSummary(groupIds, metricId);

        for (Object[] testGroupSummary : testGroupsSummary){
            Long testGroupId = (Long) testGroupSummary[3];

            for (Long testId : testMap.get(testGroupId)){
                testGroupSummary[3] = new BigInteger(testId.toString());
                testsSummary.add(testGroupSummary);
            }
        }

        return testsSummary;
    }

    /**
     * @return list of object[] (taskdata id, parent id, session id)
     */
    private static List<Object[]> getParents(EntityManager entityManager, Set<Long> taskIds){
        return entityManager.createNativeQuery("select taskData.id, workloadData.parentId, taskData.sessionId from TaskData taskData inner join " +
                "WorkloadData workloadData on taskData.taskId = workloadData.taskId " +
                " and taskData.sessionId = workloadData.sessionId where taskData.id in (:ids);").setParameter("ids", taskIds).getResultList();
    }

    /**
     * @return list of object[] (taskdata id, taskId, session id)
     */
    private static List<Object[]> getTestGroups(EntityManager entityManager, Set<Long> taskIds){
        return entityManager.createNativeQuery("select task.id, task.taskId, task.sessionId from TaskData task inner join " +
                "(select taskData.id, taskData.taskId, workloadData.parentId, taskData.sessionId from TaskData taskData " +
                "inner join WorkloadData workloadData on  taskData.taskId=workloadData.taskId " +
                "and taskData.sessionId=workloadData.sessionId  where taskData.id in (:ids)) parents " +
                "on task.taskId=parents.parentId and task.sessionId=parents.sessionId;").setParameter("ids", taskIds).getResultList();
    }

    /**
     * @return multi map <test-group id, tests ids>
     */
    private static Multimap<Long, Long> getTestsInTestGroup(List<Object[]> parents, List<Object[]> groups){
        Multimap testMap = ArrayListMultimap.create();
        for (Object[] test : parents){
            String key = (String)test[1] + (String)test[2];
            testMap.put(key, ((BigInteger) test[0]).longValue());
        }

        Multimap testsByGroupId = ArrayListMultimap.create();
        for (Object[] group : groups){
            String key = (String)group[1] + (String)group[2];
            testsByGroupId.putAll(((BigInteger) group[0]).longValue(), testMap.get(key));
        }

        return testsByGroupId;
    }

    private static boolean containsAtLeastOne(Collection origin, Collection elements){
        boolean result = false;
        for (Object element : elements){
            if (origin.contains(element)){
                result = true;
                break;
            }
        }

        return result;
    }

}
