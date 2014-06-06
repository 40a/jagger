package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.dto.SummaryMetricDto;


import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public interface MetricDataServiceAsync {
    void getMetrics(List<MetricNode> metricNames, AsyncCallback<Map<MetricNode, SummaryMetricDto>> async);
}
