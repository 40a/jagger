package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 26.03.13
 * Time: 12:30
 * Panel that contains table of metrics in comparison mod (multiple session selected)
 */
public class SessionComparisonPanel extends VerticalPanel{

    private final String TEST_DESCRIPTION = "testDescription";
    private final String TEST_NAME = "testName";
    // property to render in Metric column
    private final String NAME = "name";
    private final String SESSION_HEADER = "Session ";
    private final String SESSION_INFO_ID = "sessionInfo";
    @SuppressWarnings("all")
    private final String COMMENT = "Comment";
    @SuppressWarnings("all")
    private final int MIN_COLUMN_WIDTH = 200;
    @SuppressWarnings("all")
    private final String ONE_HUNDRED_PERCENTS = "100%";
    @SuppressWarnings("all")
    private final String START_DATE = "Start Date";
    @SuppressWarnings("all")
    private final String END_DATE = "End Date";
    @SuppressWarnings("all")
    private final String ACTIVE_KERNELS = "Active Kernels";
    @SuppressWarnings("all")
    private final String TASKS_EXECUTED = "Tasks Executed";
    @SuppressWarnings("all")
    private final String TASKS_FAILED = "Tasks Failed";

    private Collection<SessionDataDto> chosenSessions;

    private final String WHITE_SPACE_NORMAL = "white-space: normal";

    private TreeGrid<TreeItem> treeGrid;
    private TreeStore<TreeItem> treeStore = new TreeStore<TreeItem>(new ModelKeyProvider<TreeItem>() {
        @Override
        public String getKey(TreeItem item) {
            return String.valueOf(item.getKey());
        }
    });

    private HashMap<MetricNameDto, MetricDto> cache = new HashMap<MetricNameDto, MetricDto>();

    public HashMap<MetricNameDto, MetricDto> getCachedMetrics() {
        return cache;
    }

    public SessionComparisonPanel(Set<SessionDataDto> chosenSessions){
        setWidth(ONE_HUNDRED_PERCENTS);
        setHeight(ONE_HUNDRED_PERCENTS);
        this.chosenSessions = chosenSessions;
        init(chosenSessions);
    }

    private void init(Set<SessionDataDto> chosenSessions){

        treeStore.clear();
        List<ColumnConfig<TreeItem, ?>> columns = new ArrayList<ColumnConfig<TreeItem, ?>>();

        //sort sessions by number sessionId
        SortedSet<SessionDataDto> sortedSet = new TreeSet<SessionDataDto>(new Comparator<SessionDataDto>() {
            @Override
            public int compare(SessionDataDto o, SessionDataDto o2) {
                return (Long.parseLong(o.getSessionId()) - Long.parseLong(o2.getSessionId())) > 0 ? 1 : -1;
            }
        });
        sortedSet.addAll(chosenSessions);

        ColumnConfig<TreeItem, String> nameColumn =
                new ColumnConfig<TreeItem, String>(new MapValueProvider(NAME), (int)(MIN_COLUMN_WIDTH * 1.5));
        nameColumn.setHeader("Metric");
        columns.add(nameColumn);

        for (SessionDataDto session: sortedSet) {
            ColumnConfig<TreeItem, String> column = new ColumnConfig<TreeItem, String>(
                    new MapValueProvider(SESSION_HEADER + session.getSessionId())
            );
            column.setHeader(SESSION_HEADER + session.getSessionId());
            column.setWidth(MIN_COLUMN_WIDTH);
            column.setCell(new AbstractCell<String>() {
                @Override
                public void render(Context context, String value, SafeHtmlBuilder sb) {
                    if (value != null) {
                        sb.appendHtmlConstant(value);
                    }
                }
            });

            column.setColumnTextStyle(new SafeStyles() {
                @Override
                public String asString() {
                    return WHITE_SPACE_NORMAL;
                }
            });

            columns.add(column);
        }

        ColumnModel<TreeItem> cm = new ColumnModel<TreeItem>(columns);
        treeGrid = new NoIconsTreeGrid<TreeItem>(treeStore, cm, nameColumn);

        addSessionInfo(sortedSet);

        treeGrid.setAutoExpand(true);
        treeGrid.setMinColumnWidth(MIN_COLUMN_WIDTH);
        treeGrid.getView().setAutoExpandColumn(nameColumn);
        treeGrid.setAllowTextSelection(true);
        treeGrid.getTreeView().setAutoFill(true);
        add(treeGrid);
    }

    private void addSessionInfo(Set<SessionDataDto> chosenSessions) {
        TreeItem sessionInfo = new TreeItem(SESSION_INFO_ID);
        sessionInfo.put(NAME, "Session Info");
        treeStore.add(sessionInfo);

        addCommentRecord(chosenSessions, sessionInfo);
        addStartEndTimeRecords(chosenSessions, sessionInfo);
        addAdditionalRecords(chosenSessions, sessionInfo);

        treeGrid.expandAll();
    }

    private void addAdditionalRecords(Set<SessionDataDto> chosenSessions, TreeItem parent) {
        TreeItem item = new TreeItem(ACTIVE_KERNELS);
        item.put(NAME, ACTIVE_KERNELS);
        for (SessionDataDto session : chosenSessions) {
            item.put(SESSION_HEADER + session.getSessionId(), session.getActiveKernelsCount() + "");
        }
        treeStore.add(parent, item);

        item = new TreeItem(TASKS_EXECUTED);
        item.put(NAME, TASKS_EXECUTED);
        for (SessionDataDto session : chosenSessions) {
            item.put(SESSION_HEADER + session.getSessionId(), session.getTasksExecuted() + "");
        }
        treeStore.add(parent, item);

        item = new TreeItem(TASKS_FAILED);
        item.put(NAME, TASKS_FAILED);
        for (SessionDataDto session : chosenSessions) {
            item.put(SESSION_HEADER + session.getSessionId(), session.getTasksFailed() + "");
        }
        treeStore.add(parent, item);
    }

    private void addStartEndTimeRecords(Set<SessionDataDto> chosenSessions, TreeItem parent) {
        TreeItem date = new TreeItem(START_DATE);
        date.put(NAME, START_DATE);
        for (SessionDataDto session : chosenSessions) {
            date.put(SESSION_HEADER + session.getSessionId(), session.getStartDate());
        }
        treeStore.add(parent, date);

        date = new TreeItem(END_DATE);
        date.put(NAME, END_DATE);
        for (SessionDataDto session : chosenSessions) {
            date.put(SESSION_HEADER + session.getSessionId(), session.getEndDate());
        }
        treeStore.add(parent, date);
    }

    private void addCommentRecord(Set<SessionDataDto> chosenSessions, TreeItem parent) {

        TreeItem comment = new TreeItem(COMMENT);
        comment.put(NAME, COMMENT);
        for (SessionDataDto session : chosenSessions) {
            comment.put(SESSION_HEADER + session.getSessionId(), session.getComment());
        }
        treeStore.add(parent, comment);

    }


    // // to make columns fit 100% width if grid created not on Summary Tab
    public void refresh() {
        treeGrid.getView().refresh(true);
    }

    // clear everything but Session Information
    public void clearTreeStore() {

        for (TreeItem root : treeStore.getRootItems()) {
            if (root.getKey().equals(SESSION_INFO_ID)) {
                continue;
            }
            treeStore.remove(root);
        }
    }


    public void addMetricRecord(MetricDto metricDto) {

        cache.put(metricDto.getMetricName(), metricDto);
        TreeItem record = new TreeItem(metricDto);
        addItemToStore(record);
        treeGrid.expandAll();
    }


    public void addMetricRecords(List<MetricDto> loaded) {
        for (MetricDto metric : loaded) {
            addMetricRecord(metric);
        }
    }

    private void addItemToStore(TreeItem record) {

        String descriptionString = record.get(TEST_DESCRIPTION);
        String testNameString = record.get(TEST_NAME);
        String metricName = record.get(NAME);

        if (descriptionString == null || testNameString == null)
            return;

        for (TreeItem testDescriptionPath : treeStore.getRootItems()) {

            if (testDescriptionPath.get(NAME).equals(descriptionString)) {

                for (TreeItem testName : treeStore.getAllChildren(testDescriptionPath)) {
                    if (testName.get(NAME).equals(testNameString)) {
                        for (TreeItem rec : treeStore.getChildren(testName)) {
                            if (rec.get(NAME).equals(metricName)) {
                                return;
                            }
                        }
                        treeStore.add(testName, record);
                        return;
                    }
                }
                //create new TestNamePath
                TreeItem testName = new TreeItem(testDescriptionPath.getKey() + testNameString);
                testName.put(NAME, testNameString);
                testName.put(TEST_DESCRIPTION, descriptionString);
                treeStore.add(testDescriptionPath, testName);
                treeStore.add(testName, record);
                return;
            }
        }

        // create new TestDescriptionPath
        TreeItem testDescription = new TreeItem("descriptionItem:" + descriptionString);
        testDescription.put(NAME, descriptionString);
        treeStore.add(testDescription);
        TreeItem testName = new TreeItem(descriptionString + testNameString);
        testName.put(NAME, testNameString);
        testName.put(TEST_DESCRIPTION, descriptionString);
        treeStore.add(testDescription, testName);
        treeStore.add(testName, record);
    }


    public void removeRecords(List<MetricDto> list) {

        for (MetricDto metric : list) {
            removeRecord(metric);
        }
    }

    private void removeRecord(MetricDto metric) {

        String description = metric.getMetricName().getTests().getDescription();
        String testName = metric.getMetricName().getTests().getTaskName();
        String metricName = metric.getMetricName().getName();

        for (TreeItem root : treeStore.getRootItems()) {
            if (description.equals(root.get(NAME))) {
                for (TreeItem child : treeStore.getChildren(root)) {
                    if (testName.equals(child.get(NAME))) {
                        for (TreeItem record : treeStore.getChildren(child)) {
                            if (metricName.equals(record.get(NAME))) {
                                treeStore.remove(record);
                                if (!treeStore.hasChildren(child)) {
                                    treeStore.remove(child);
                                    if (!treeStore.hasChildren(root)) {
                                        treeStore.remove(root);
                                    }
                                }
                                return;
                            }
                        }
                        return;
                    }
                }
                return;
            }
        }
    }

    public void updateTests(Collection<TaskDataDto> tests) {
        for (TaskDataDto test : tests) {
            addTestInfo(test);
        }
        treeGrid.expandAll();
    }

    private void addTestInfo(TaskDataDto test) {
        TreeItem testItem = getTestItem(test);

        TreeItem testInfo = new TreeItem(test.getTaskName() + "TestInfo");
        testInfo.put(NAME, "Test Info");
        treeStore.add(testItem, testInfo);

        TreeItem clock = new TreeItem(testItem.getKey() + "Clock");
        clock.put(NAME, "Clock");
        for (SessionDataDto session : chosenSessions) {
            clock.put(SESSION_HEADER + session.getSessionId(), test.getClock());
        }
        treeStore.add(testInfo, clock);

        TreeItem termination = new TreeItem(testItem.getKey() + "Termination");
        termination.put(NAME, "Termination");
        for (SessionDataDto session : chosenSessions) {
            termination.put(SESSION_HEADER + session.getSessionId(), test.getTerminationStrategy());
        }
        treeStore.add(testInfo, termination);
    }

    private TreeItem getTestItem(TaskDataDto test) {
        TreeItem description = getTestDescriptionItem(test);
        for (TreeItem item : treeStore.getChildren(description)) {
            if (test.getTaskName().equals(item.get(TEST_NAME))) {
                return item;
            }
        }
        TreeItem taskName = new TreeItem(test.getDescription() + test.getTaskName());
        taskName.put(NAME, test.getTaskName());
        treeStore.add(description, taskName);
        return taskName;
    }

    private TreeItem getTestDescriptionItem(TaskDataDto test) {
        for (TreeItem item : treeStore.getRootItems()) {
            if (test.getDescription().equals(item.get(TEST_DESCRIPTION))) {
                return item;
            }
        }
        TreeItem description = new TreeItem(test.getDescription());
        description.put(NAME, test.getDescription());
        treeStore.add(description);
        return description;
    }

    private class NoIconsTreeGrid<M> extends TreeGrid<M> {


        public NoIconsTreeGrid(TreeStore<M> store, ColumnModel<M> cm, ColumnConfig<M, ?> treeColumn) {
            super(store, cm, treeColumn);
        }

        @Override
        protected ImageResource calculateIconStyle(M model) {
            return null;
        }
    }

    private class TreeItem extends HashMap<String, String> {

        String key;

        private String getKey() {
            return key;
        }

        @SuppressWarnings("unused")
        public TreeItem() {}

        public TreeItem(String key) {
            this.key = key;
        }

        public TreeItem(MetricDto metricDto) {

            MetricNameDto metricName = metricDto.getMetricName();
            this.key = metricName.getTests().getDescription() + metricName.getTests().getTaskName() + metricName.getName();
            put(NAME, metricName.getName());
            put(TEST_DESCRIPTION, metricName.getTests().getDescription());
            put(TEST_NAME, metricName.getTests().getTaskName());

            for (MetricValueDto metricValue : metricDto.getValues()) {
                put(SESSION_HEADER + metricValue.getSessionId(), metricValue.getValueRepresentation());
            }
        }
    }

    private class MapValueProvider implements ValueProvider<TreeItem, String> {
        private String field;

        public MapValueProvider(String field) {
            this.field = field;
        }

        @Override
        public String getValue(TreeItem object) {
            return object.get(field);
        }

        @Override
        public void setValue(TreeItem object, String value) {
            object.put(field, value);
        }

        @Override
        public String getPath() {
            return field;
        }
    }
}
