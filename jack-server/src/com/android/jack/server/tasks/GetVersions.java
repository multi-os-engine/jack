/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.jack.server.tasks;

import com.android.jack.server.HasVersion;
import com.android.jack.server.JackHttpServer;
import com.android.sched.util.Version;

import org.simpleframework.http.ContentType;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.parse.ContentTypeParser;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

abstract class GetVersions extends SynchronousAdministrativeTask {

  @Nonnull
  private static final Logger logger = Logger.getLogger(GetVersions.class.getName());
  @Nonnull
  private final String name;

  GetVersions(@Nonnull String name, @Nonnull JackHttpServer jackServer) {
    super(jackServer);
    this.name = name;
  }

  @Override
  protected void handle(long taskId, @Nonnull Request request, @Nonnull Response response) {
    logger.log(Level.INFO, "Get " + name + " version(s)");
    ContentType expectedContentType = new ContentTypeParser(request.getValue("accept"));
    String charset = expectedContentType.getParameter("charset");
    if (charset == null) {
      charset = StandardCharsets.US_ASCII.name();
    }
    response.setContentType("text/plain; Charset=" + charset);
    OutputStreamWriter out = null;
    try {
      out = new OutputStreamWriter(response.getOutputStream(), Charset.forName(charset));
      for (HasVersion versioned : getVersionnedElements()) {
        Version version = versioned.getVersion();
        out.append(version.getReleaseCode() + "." + version.getSubReleaseCode() + "."
            + version.getSubReleaseKind().name() + "\n");
      }
      response.setStatus(Status.OK);
    } catch (UnsupportedCharsetException e) {
      logger.log(Level.WARNING,
          "Unsupported charset for content type '" + expectedContentType + "'", e);
      response.setStatus(Status.NOT_ACCEPTABLE);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to write response", e);
      response.setStatus(Status.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to close response body", e);
        response.setStatus(Status.INTERNAL_SERVER_ERROR);
      }
    }
  }

  protected abstract Collection<? extends HasVersion> getVersionnedElements();
}