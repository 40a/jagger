package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.JsArray;
import com.googlecode.gflot.client.DataPoint;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.SimplePlot;
import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.PointDto;
import com.griddynamics.jagger.dbapi.model.LegendNode;
import com.griddynamics.jagger.webclient.client.components.control.LegendNodeCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;

/**
 * Implementation of AbstractTree that represents interactive legend as tree.
 */
public class LegendTree extends AbstractTree<LegendNode, LegendNode> {

    /**
     * Plot that would controlled with Legend tree
     */
    private SimplePlot plot;

    /**
     * Plots panel where plot is situated
     */
    private PlotsPanel plotsPanel;


    private final static ValueProvider<LegendNode, LegendNode> VALUE_PROVIDER =  new ValueProvider<LegendNode, LegendNode>() {

        @Override
        public LegendNode getValue(LegendNode object) {
            return object;
        }

        @Override
        public void setValue(LegendNode object, LegendNode value) {
            object.setDisplayName(value.getDisplayName());
            object.setMetricNameDtoList(value.getMetricNameDtoList());
            object.setId(value.getId());
            object.setLine(value.getLine());
        }

        @Override
        public String getPath() {
            return "legend";
        }
    };

    /**
     * Constructor matches super class
     */
    public LegendTree(SimplePlot plot, PlotsPanel plotsPanel) {
        super(
                new TreeStore<LegendNode>(new ModelKeyProvider<LegendNode>() {
                    @Override
                    public String getKey(LegendNode item) {
                        return item.getId();
                    }
                }),
                VALUE_PROVIDER);
        this.plot = plot;
        this.plotsPanel = plotsPanel;

        this.setAutoExpand(true);
        this.setCell(LegendNodeCell.getInstance());
    }

    @Override
    protected void check(LegendNode item, CheckState state) {
        noRedrawCheck(item, state);
        redrawPlot();
    }

    /**
     * Adds or removes lines without redrawing plot. Changes can't be seen.
     * @param item chosen item
     * @param state check state
     */
    private void noRedrawCheck(LegendNode item, CheckState state) {
        PlotSingleDto plotSingleDto = item.getLine();

        if (plotSingleDto != null) {

            if (state == CheckState.CHECKED) {

                Series series = Series.create().setId(item.getId()).setColor(plotSingleDto.getColor());
                SeriesHandler sh = plot.getModel().addSeries(series);
                for (PointDto point: plotSingleDto.getPlotData()) {
                    sh.add(DataPoint.of(point.getX(), point.getY()));
                }

            } else if (state == CheckState.UNCHECKED) {

                // remove curve from view
                JsArray<Series> seriesArray = plot.getModel().getSeries();
                int k;
                for (k = 0; k < seriesArray.length(); k++) {
                    Series curSeries = seriesArray.get(k);
                    // label used as id
                    if (curSeries.getId().equals(item.getId())) {
                        // found
                        break;
                    }
                }
                if (k < seriesArray.length()) {
                    plot.getModel().removeSeries(k);
                }
            }
        } else {
            for (LegendNode child : store.getAllChildren(item)) {
                noRedrawCheck(child, state);
            }
        }
    }

    /**
     * Redraw plot with specific axis ranges
     */
    private void redrawPlot() {
        if (!plotsPanel.isEmpty()) {
            double minXVisible = plotsPanel.getMinXAxisVisibleValue();
            double maxXVisible = plotsPanel.getMaxXAxisVisibleValue();

            if (plot.isAttached()) {
                double minYVisible = plot.getAxes().getY().getMinimumValue();
                double maxYVisible = plot.getAxes().getY().getMaximumValue();

                // save y axis range for plot from very start
                plot.getOptions().getYAxisOptions().setMinimum(minYVisible).setMaximum(maxYVisible);
            }

            // set x axis in range as all other plots
            plot.getOptions().getXAxisOptions().setMinimum(minXVisible).setMaximum(maxXVisible);
            plot.redraw();
        }
    }
}
