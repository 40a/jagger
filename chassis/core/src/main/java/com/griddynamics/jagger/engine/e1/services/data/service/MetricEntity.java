package com.griddynamics.jagger.engine.e1.services.data.service;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;

//??? has only summary or plot



/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/5/13
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetricEntity {
    private MetricNameDto metricNameDto;
    private boolean summaryAvailable = false;
    private boolean plotAvailable = false;
    //???
//    private String metricId;
//    private String displayName;
//???    private Double summaryValue;

    public void setMetricNameDto(MetricNameDto metricNameDto) {
        this.metricNameDto = metricNameDto;
    }
    public MetricNameDto getMetricNameDto() {
        return metricNameDto;
    }

    public String getMetricId() {
        return metricNameDto.getMetricName();
    }

//    public void setMetricId(String metricId) {
//        this.metricId = metricId;
//    }

    public String getDisplayName() {
        return metricNameDto.getMetricDisplayName();
    }

    public MetricNameDto.Origin getOrigin() {
        return metricNameDto.getOrigin();
    }

    public boolean isSummaryAvailable() {
        return summaryAvailable;
    }

    public void setSummaryAvailable(boolean summaryAvailable) {
        this.summaryAvailable = summaryAvailable;
    }

    public boolean isPlotAvailable() {
        return plotAvailable;
    }

    public void setPlotAvailable(boolean plotAvailable) {
        this.plotAvailable = plotAvailable;
    }

//    public void setDisplayName(String displayName) {
//        this.displayName = displayName;
//    }

    //???
//    public Double getSummaryValue() {
//        return summaryValue;
//    }
//
//    public void setSummaryValue(Double summaryValue) {
//        this.summaryValue = summaryValue;
//    }







    //???
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        MetricEntity entity = (MetricEntity) o;
//
//        if (displayName != null ? !displayName.equals(entity.displayName) : entity.displayName != null) return false;
//        if (metricId != null ? !metricId.equals(entity.metricId) : entity.metricId != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = metricId != null ? metricId.hashCode() : 0;
//        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
//        return result;
//    }
}
