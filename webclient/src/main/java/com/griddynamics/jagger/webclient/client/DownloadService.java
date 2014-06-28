package com.griddynamics.jagger.webclient.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.dbapi.dto.PlotIntegratedDto;


@RemoteServiceRelativePath("rpc/DownloadService")
public interface DownloadService extends RemoteService {

    public static class Async {
        private static final DownloadServiceAsync ourInstance = (DownloadServiceAsync) GWT.create(DownloadService.class);

        public static DownloadServiceAsync getInstance() {
            return ourInstance;
        }
    }

    /**
     * Creates csv file representing plot on server side and send back key for created file
     * @param plot that should be represented
     * @return key of created file
     * @throws RuntimeException */
    public String createPlotCsvFile(PlotIntegratedDto plot) throws RuntimeException;
}
