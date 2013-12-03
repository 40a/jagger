package com.griddynamics.jagger.engine.e1.collector;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/30/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetricDescription implements Serializable{

    protected String id;
    protected String displayName;
    protected boolean showSummary = true;
    protected boolean showPlotData;
    protected List<MetricAggregatorProvider> aggregators = Lists.newArrayList();

    public MetricDescription(String metricId) {
        this.id = metricId;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAggregators(List<MetricAggregatorProvider> aggregators) {
        this.aggregators = aggregators;
    }

    public List<MetricAggregatorProvider> getAggregators() {
        return aggregators;
    }

    public boolean getShowSummary() {
        return showSummary;
    }

    public void setShowSummary(boolean showSummary) {
        this.showSummary = showSummary;
    }

    public boolean getShowPlotData() {
        return showPlotData;
    }

    public void setShowPlotData(boolean showPlot) {
        this.showPlotData = showPlot;
    }

    public MetricDescription showSummary(boolean show){
        this.showSummary = show;
        return this;
    }

    public MetricDescription plotData(boolean show){
        this.showPlotData = show;
        return this;
    }

    public MetricDescription displayName(String name){
        this.displayName = name;
        return this;
    }

    public MetricDescription addAggregator(MetricAggregatorProvider provider){
        aggregators.add(provider);
        return this;
    }
}
