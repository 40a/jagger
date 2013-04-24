package com.griddynamics.jagger.webclient.client.data;

import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 22.04.13
 * Time: 10:15
 * To change this template use File | Settings | File Templates.
 */
public class MetricRankingProvider {
    private static List<String> patterns = Arrays.asList(
            "Iterations",
            "Duration",
            "Throughput",
            "Latency",
            "Success rate",
            "Latency .+ %"
    );

    protected static int compare(String o1, String o2){
        Comparable o1Rank = getRank(o1);
        Comparable o2Rank = getRank(o2);
        if (o1Rank.compareTo(0)==0 && o2Rank.compareTo(0)==0){
            return o1.compareTo(o2);
        }
        return o1Rank.compareTo(o2Rank);
    }

    protected static Integer getRank(String o){
        int rank = 0;
        for (String pattern : patterns){
            if (pattern.matches(o)){
                return new Integer(patterns.size()-rank);
            }
            rank++;
        }
        return 0;
    }

    public static void sortMetricNames(List<MetricNameDto> list){
        Collections.sort(list, new Comparator<MetricNameDto>() {
            @Override
            public int compare(MetricNameDto o, MetricNameDto o2) {
                return (-1)*MetricRankingProvider.compare(o.getName(), o2.getName());
            }
        });
    }

    public static void sortMetrics(List<MetricDto> list){
        Collections.sort(list, new Comparator<MetricDto>() {
            @Override
            public int compare(MetricDto metricDto, MetricDto metricDto2) {
                return (-1)*MetricRankingProvider.compare(metricDto.getMetricName().getName(), metricDto2.getMetricName().getName());
            }
        });
    }
}
