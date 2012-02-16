/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.storage.fs.logging;

import com.caucho.hessian.io.Hessian2Output;
import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.griddynamics.jagger.storage.FileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author Alexey Kiselyov, Vladimir Shulga
 *         Date: 20.07.11
 */
public class BufferedLogWriter implements LogWriter {
    private final Logger log = LoggerFactory.getLogger(BufferedLogWriter.class);
    private final Multimap<LogFile, Serializable> queue;
    private FileStorage fileStorage;
    private int flushSize;
    private volatile boolean isFlushInProgress;
    private final Object semaphore = new Object();

    {
        Multimap<LogFile, Serializable> tmp = LinkedHashMultimap.create();
        queue = Multimaps.synchronizedMultimap(tmp);
    }

    @Override
    public void log(String sessionId, String dir, String logOwner, Serializable logEntry) {
        Preconditions.checkNotNull(logEntry, "Null is not supported");

        synchronized (semaphore) {
            queue.put(new LogFile(sessionId, dir, logOwner), logEntry);
        }

        if (!isFlushInProgress && (queue.size() >= flushSize)) {
            flush();
        }
    }

    @Override
    public synchronized void flush() {
        long startTime = System.currentTimeMillis();
        try {
            isFlushInProgress = true;
            Multimap<LogFile, Serializable> forFlush;
            synchronized (semaphore) {
                forFlush = LinkedHashMultimap.create(queue);
                queue.clear();
            }

            log.debug("Flush queue. flushId {}. Current queue size is {}", startTime, forFlush.size());
            for (LogFile logFile : forFlush.keySet()) {
                Collection<Serializable> fileQueue = forFlush.get(logFile);
                if (fileQueue.isEmpty()) {
                    continue;
                }
                String path = logFile.getPath();
                Hessian2Output objectStream = null;
                OutputStream os = null;
                try {
                    if (this.fileStorage.exists(path)) {
                        os = this.fileStorage.append(path);
                    } else {
                        os = this.fileStorage.create(path);
                    }
                    objectStream = new Hessian2Output(os);
                    for (Serializable logEntry : fileQueue) {
                        objectStream.writeObject(logEntry);
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                } finally {
                    if (objectStream != null) {
                        try {
                            objectStream.close();
                            os.close();
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        } finally {
            log.debug("Flush queue finished. flushId {}. Flush took: {}", startTime, (System.currentTimeMillis() - startTime));
            isFlushInProgress = false;
        }
    }

    @Required
    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Required
    public void setFlushSize(int flushSize) {
        this.flushSize = flushSize;
    }

}
