/*
 * Copyright (c) 2010-2012 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.asynchttpclient.async;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.Response;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Benjamin Hanzelmann
 */
public abstract class PutLargeFileTest extends AbstractBasicTest {

    @Test(groups = { "standalone", "default_provider" }, enabled = true)
    public void testPutLargeFile() throws Exception {

        long repeats = (1024 * 1024 * 100 / PATTERN_BYTES.length) + 1;
        File file = createTempFile(PATTERN_BYTES, (int) repeats);

        int timeout = (int) (repeats / 1000);

        AsyncHttpClient client = getAsyncHttpClient(new AsyncHttpClientConfig.Builder().setConnectionTimeoutInMs(timeout).build());
        try {
            Response response = client.preparePut(getTargetUrl()).setBody(file).execute().get();
            Assert.assertEquals(200, response.getStatusCode());
        } finally {
            client.close();
        }
    }

    @Test(groups = { "standalone", "default_provider" })
    public void testPutSmallFile() throws Exception {

        long repeats = (1024 / PATTERN_BYTES.length) + 1;
        File file = createTempFile(PATTERN_BYTES, (int) repeats);

        AsyncHttpClient client = getAsyncHttpClient(null);
        try {
            Response response = client.preparePut(getTargetUrl()).setBody(file).execute().get();
            Assert.assertEquals(200, response.getStatusCode());
        } finally {
            client.close();
        }
    }

    @Override
    public AbstractHandler configureHandler() throws Exception {
        return new AbstractHandler() {

            public void handle(String arg0, Request arg1, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

                resp.setStatus(200);
                resp.getOutputStream().flush();
                resp.getOutputStream().close();

                arg1.setHandled(true);
            }
        };
    }
}
