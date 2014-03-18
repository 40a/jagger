package com.griddynamics.jagger.webclient.server.plot;

import com.google.common.collect.Multimap;
import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import com.griddynamics.jagger.util.AgentUtils;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.server.CommonUtils;
import com.griddynamics.jagger.webclient.server.DataProcessingUtil;
import com.griddynamics.jagger.webclient.server.fetch.FetchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 7/12/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomMetricPlotNameProvider {

    Logger log = LoggerFactory.getLogger(CustomMetricPlotNameProvider.class);

    private FetchUtil fetchUtil;
    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setFetchUtil(FetchUtil fetchUtil) {
        this.fetchUtil = fetchUtil;
    }

    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    public Set<MetricNameDto> getPlotNames(List<TaskDataDto> taskDataDtos){

        long temp = System.currentTimeMillis();
        Set<MetricNameDto> result = new HashSet<MetricNameDto>();

        result.addAll(getPlotNamesNewModel(taskDataDtos));

        result.addAll(getTestGroupPlotNamesNewModel(taskDataDtos));

        result.addAll(getPlotNamesOldModel(taskDataDtos));

        log.debug("{} ms spent to fetch custom metrics plots names in count of {}", System.currentTimeMillis() - temp, result.size());

        return result;
    }


    public Set<MetricNameDto> getPlotNamesOldModel(List<TaskDataDto> taskDataDtos){

        Set<Long> testIds = new HashSet<Long>();
        for (TaskDataDto tdd : taskDataDtos) {
            testIds.addAll(tdd.getIds());
        }

        // check old model (before jagger 1.2.4)
        List<Object[]> plotNames = entityManager.createNativeQuery("select metricDetails.metric, metricDetails.taskData_id from MetricDetails metricDetails " +
                "where metricDetails.taskData_id in (:ids) " +
                "group by metricDetails.metric, metricDetails.taskData_id")
                .setParameter("ids", testIds)
                .getResultList();


        if (plotNames.isEmpty()) {
            return Collections.emptySet();
        }

        Set<MetricNameDto> result = new HashSet<MetricNameDto>(plotNames.size());

        for (Object[] plotName : plotNames){
            if (plotName != null) {
                for (TaskDataDto tdd : taskDataDtos) {
                    if (tdd.getIds().contains(((BigInteger)plotName[1]).longValue())) {
                        MetricNameDto metricNameDto = new MetricNameDto(tdd, (String)plotName[0]);
                        metricNameDto.setOrigin(MetricNameDto.Origin.METRIC);
                        result.add(metricNameDto);
                    }
                }
            }
        }

        return result;
    }


    public Set<MetricNameDto> getPlotNamesNewModel(List<TaskDataDto> taskDataDtos){

        try {
            Set<Long> testIds = CommonUtils.getTestsIds(taskDataDtos);

            List<Object[]> plotNamesNew = getMetricNames(testIds);

            if (plotNamesNew.isEmpty()) {
                return Collections.EMPTY_SET;
            }

            Set<MetricNameDto> result = new HashSet<MetricNameDto>(plotNamesNew.size());

            for (Object[] plotName : plotNamesNew){
                if (plotName != null) {
                    for (TaskDataDto tdd : taskDataDtos) {
                        if (tdd.getIds().contains((Long)plotName[2])) {
                            result.add(new MetricNameDto(tdd, (String)plotName[0], (String)plotName[1], MetricNameDto.Origin.METRIC));
                        }
                    }
                }
            }

            return result;
        } catch (PersistenceException e) {
            log.debug("Could not fetch metric plot names from MetricPointEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.EMPTY_SET;
        }

    }

    //???
    // un db session #35 testGroup is empty => groupIds empty => loadTestsMetricDescriptions fails
    //??? left before merging
    public Set<MetricNameDto> getTestGroupPlotNamesNewModel(List<TaskDataDto> tests){

        try {
            Set<Long> testIds = CommonUtils.getTestsIds(tests);

            Multimap<Long, Long> testGroupMap = fetchUtil.getTestsInTestGroup(testIds);

            List<Object[]> plotNamesNew = getMetricNames(testGroupMap.keySet());

            plotNamesNew = filterMonitoring(plotNamesNew);

            if (plotNamesNew.isEmpty()) {
                return Collections.EMPTY_SET;
            }

            Set<MetricNameDto> result = new HashSet<MetricNameDto>(plotNamesNew.size());

            for (Object[] mde : plotNamesNew){
                for (TaskDataDto td : tests){
                    Collection<Long> allTestsInGroup = testGroupMap.get((Long)mde[2]);
                    if (CommonUtils.containsAtLeastOne(td.getIds(), allTestsInGroup)){
                        result.add(new MetricNameDto(td, (String)mde[0], (String)mde[1], MetricNameDto.Origin.METRIC_GROUP));
                    }
                }
            }

            return result;
        } catch (PersistenceException e) {
            log.debug("Could not fetch test-group metric plot names from MetricPointEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.EMPTY_SET;
        }
    }

    private List<Object[]> getMetricNames(Set<Long> testIds){
        //??? can come empty list
        if (testIds.size() > 0) {
            return entityManager.createQuery(
                    "select mpe.metricDescription.metricId, mpe.metricDescription.displayName, mpe.metricDescription.taskData.id " +
                            "from MetricPointEntity as mpe where mpe.metricDescription.taskData.id in (:taskIds) group by mpe.metricDescription.id")
                    .setParameter("taskIds", testIds)
                    .getResultList();
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }

    private List<Object[]> filterMonitoring(List<Object[]> origin){
        List<Object[]> result = new ArrayList<Object[]>(origin.size());

        // monitoring ids in reporting
        Set<String> reportingMonitoring = new HashSet<String>();
        for (DefaultMonitoringParameters[] value : monitoringPlotGroups.values()){
            for (DefaultMonitoringParameters parameter : value){
                reportingMonitoring.add(parameter.getId());
            }
        }

        // all monitoring ids
        Set<String> allMonitoring = new HashSet<String>();
        for (DefaultMonitoringParameters parameter : DefaultMonitoringParameters.values()){
            allMonitoring.add(parameter.getId());
        }

        for (Object[] row : origin){
            String metricId = getMonitoringId((String)row[0]);
            if (allMonitoring.contains(metricId) && !reportingMonitoring.contains(metricId)){
                // ignore this metrics
                continue;
            }
            result.add(row);
        }

        return result;
    }

    private String getMonitoringId(String origin){
        String[] splitName = AgentUtils.splitMonitoringMetricId(origin);
        if (splitName.length > 1) {
            return splitName[0];
        }

        return origin;
    }

}
