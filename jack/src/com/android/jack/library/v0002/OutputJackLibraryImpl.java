/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.jack.library.v0002;

import com.google.common.collect.ImmutableSet;

import com.android.jack.library.DumpInLibrary;
import com.android.jack.library.FileType;
import com.android.jack.library.FileTypeDoesNotExistException;
import com.android.jack.library.JackLibrary;
import com.android.jack.library.JackLibraryFactory;
import com.android.jack.library.LibraryIOException;
import com.android.jack.library.OutputJackLibrary;
import com.android.jack.library.OutputLibrary;
import com.android.jack.library.OutputLibraryLocation;
import com.android.sched.util.config.Config;
import com.android.sched.util.config.ThreadConfig;
import com.android.sched.util.config.id.PropertyId;
import com.android.sched.util.file.CannotCreateFileException;
import com.android.sched.util.file.CannotDeleteFileException;
import com.android.sched.util.file.NoSuchFileException;
import com.android.sched.util.file.NotDirectoryException;
import com.android.sched.util.file.NotFileOrDirectoryException;
import com.android.sched.util.location.Location;
import com.android.sched.util.log.LoggerFactory;
import com.android.sched.vfs.GenericInputOutputVFS;
import com.android.sched.vfs.GenericInputVFS;
import com.android.sched.vfs.GenericOutputVFS;
import com.android.sched.vfs.InputOutputVFS;
import com.android.sched.vfs.InputOutputVFile;
import com.android.sched.vfs.InputVFS;
import com.android.sched.vfs.InputVFile;
import com.android.sched.vfs.MessageDigestFS;
import com.android.sched.vfs.OutputVFile;
import com.android.sched.vfs.PrefixedFS;
import com.android.sched.vfs.VFS;
import com.android.sched.vfs.VPath;
import com.android.sched.vfs.WrongVFSFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

/**
 * Jack library generated by Jack.
 */
public class OutputJackLibraryImpl extends OutputJackLibrary {

  @Nonnull
  private static Logger logger = LoggerFactory.getLogger();

  private boolean closed = false;

  private final boolean generateJacklibDigest =

      ThreadConfig.get(JackLibraryFactory.GENERATE_JACKLIB_DIGEST).booleanValue();

  @Nonnull
  private final Map<FileType, InputOutputVFS> sectionVFS =
      new EnumMap<FileType, InputOutputVFS>(FileType.class);

  @Nonnull
  private final OutputLibraryLocation location = new OutputLibraryLocation() {
    @Override
    @Nonnull
    public String getDescription() {
      return getVFSLocation().getDescription();
    }

    @Override
    @Nonnull
    public OutputLibrary getOutputLibrary() {
      return OutputJackLibraryImpl.this;
    }

    @Override
    protected Location getVFSLocation() {
      return vfs.getLocation();
    }
  };

  public OutputJackLibraryImpl(@Nonnull VFS vfs, @Nonnull String emitterId,
      @Nonnull String emitterVersion) {
    super(new Properties(), vfs);
    locationList.add(location);

    try {
      loadLibraryProperties(new GenericInputVFS(vfs));
    } catch (NoSuchFileException e) {
      // Jack library not created from an existing library
    }

    putProperty(KEY_LIB_EMITTER, emitterId);
    putProperty(KEY_LIB_EMITTER_VERSION, emitterVersion);
    putProperty(KEY_LIB_MAJOR_VERSION, String.valueOf(getMajorVersion()));
    putProperty(KEY_LIB_MINOR_VERSION, String.valueOf(getMinorVersion()));
  }

  @Override
  @Nonnull
  public OutputVFile createFile(@Nonnull FileType fileType, @Nonnull final VPath typePath)
      throws CannotCreateFileException {
    assert !isClosed();
    putProperty(buildPropertyName(fileType, null /* suffix */), String.valueOf(true));
    addFileType(fileType);
    return getSectionVFS(fileType).getRootOutputVDir()
        .createOutputVFile(buildFileVPath(fileType, typePath));
  }

  @Override
  public boolean needsSequentialWriting() {
    return vfs.needsSequentialWriting();
  }

  @Override
  @Nonnull
  public OutputLibraryLocation getLocation() {
    return location;
  }

  @Nonnull
  private synchronized InputOutputVFS getSectionVFS(@Nonnull FileType fileType)
      throws CannotCreateFileException {
    InputOutputVFS currentSectionVFS;
    if (sectionVFS.containsKey(fileType)) {
      currentSectionVFS = sectionVFS.get(fileType);
    } else {
      VPath prefixPath = InputJackLibraryImpl.getSectionPath(fileType);
      VFS outputVFS = null;
      try {
        outputVFS = new PrefixedFS(vfs, prefixPath);

        if (generateJacklibDigest && fileType == FileType.PREBUILT) {

          outputVFS = new MessageDigestFS(outputVFS,
              ThreadConfig.get(JackLibraryFactory.MESSAGE_DIGEST_ALGO));
        }
      } catch (WrongVFSFormatException e) {
        // if library is well formed and digest exists this exception can not be triggered
        throw new AssertionError(e);
      } catch (NotDirectoryException e) {
        // if library is well formed this exception can not be triggered
        throw new AssertionError(e);
      }
      currentSectionVFS = new GenericInputOutputVFS(outputVFS);
      sectionVFS.put(fileType, currentSectionVFS);
    }
    return currentSectionVFS;
  }

  @Override
  public synchronized void close() throws LibraryIOException {
    if (!closed) {
      GenericOutputVFS goVFS = null;

      // Write configuration properties in library
      Config config = ThreadConfig.getConfig();
      Collection<PropertyId<?>> properties = config.getPropertyIds();
      for (PropertyId<?> property : properties) {
        if (property.hasCategory(DumpInLibrary.class)) {
          libraryProperties.put("config." + property.getName(), config.getAsString(property));
        }
      }

      try {
        goVFS = new GenericOutputVFS(vfs);
        OutputVFile libraryPropertiesOut =
            goVFS.getRootOutputVDir().createOutputVFile(LIBRARY_PROPERTIES_VPATH);

        OutputStream propertiesOS = null;
        try {
          propertiesOS = libraryPropertiesOut.getOutputStream();
          libraryProperties.store(propertiesOS, "Library properties");
        } finally {
          if (propertiesOS != null) {
            propertiesOS.close();
          }
        }
        try {
          for (InputOutputVFS intputOutputVFS : sectionVFS.values()) {
            intputOutputVFS.close();
          }
        } catch (IOException e) {
          throw new LibraryIOException(getLocation(), e);
        }
      } catch (CannotCreateFileException e) {
        throw new LibraryIOException(getLocation(), e);
      } catch (IOException e) {
        throw new LibraryIOException(getLocation(), e);
      } finally {
        try {
          if (goVFS != null) {
            goVFS.close();
          }
        } catch (IOException e) {
          throw new LibraryIOException(getLocation(), e);
        }
      }


      closed = true;
    }
  }

  @Override
  public int getMinorVersion() {
    return Version.MINOR;
  }

  @Override
  public int getMajorVersion() {
    return Version.MAJOR;
  }

  @Override
  @Nonnull
  public Iterator<InputVFile> iterator(@Nonnull FileType fileType) {
    if (!containsFileType(fileType)) {
      return ImmutableSet.<InputVFile>of().iterator();
    }

    List<InputVFile> inputVFiles = new ArrayList<InputVFile>();
    try {
      fillFiles(getSectionVFS(fileType).getRootInputVDir(), inputVFiles);
    } catch (CannotCreateFileException e) {
      // we already checked that the library contained the file type
      throw new AssertionError(e);
    }
    return inputVFiles.listIterator();
  }

  @Override
  @Nonnull
  public InputOutputVFile getFile(@Nonnull FileType fileType, @Nonnull VPath typePath)
      throws FileTypeDoesNotExistException {
    try {
      return getSectionVFS(fileType).getRootInputOutputVDir()
          .getInputVFile(buildFileVPath(fileType, typePath));
    } catch (NotFileOrDirectoryException e) {
      throw new FileTypeDoesNotExistException(getLocation(), typePath, fileType);
    } catch (NoSuchFileException e) {
      throw new FileTypeDoesNotExistException(getLocation(), typePath, fileType);
    } catch (CannotCreateFileException e) {
      throw new FileTypeDoesNotExistException(getLocation(), typePath, fileType);
    }
  }

  @Override
  @Nonnull
  public void delete(@Nonnull FileType fileType, @Nonnull VPath typePath)
      throws CannotDeleteFileException, FileTypeDoesNotExistException {
    assert !isClosed();
    try {
      getSectionVFS(fileType).getRootInputOutputVDir()
          .getInputVFile(buildFileVPath(fileType, typePath)).delete();
    } catch (NotFileOrDirectoryException e) {
      throw new FileTypeDoesNotExistException(getLocation(), typePath, fileType);
    } catch (CannotCreateFileException e) {
      throw new FileTypeDoesNotExistException(getLocation(), typePath, fileType);
    } catch (NoSuchFileException e) {
      throw new FileTypeDoesNotExistException(getLocation(), typePath, fileType);
    }
  }

  @Override
  @Nonnull
  public String getPath() {
    return vfs.getPath();
  }

  @Nonnull
  public VPath buildFileVPath(@Nonnull FileType fileType, @Nonnull VPath vpath) {
    VPath clonedPath = vpath.clone();
    clonedPath.addSuffix(InputJackLibraryImpl.getExtension(fileType));
    return clonedPath;
  }

  @Override
  @Nonnull
  protected String getPropertyPrefix(@Nonnull FileType type) {
    return InputJackLibraryImpl.getPropertyPrefixImpl(type);
  }

  private synchronized boolean isClosed() {
    return closed;
  }

  @Nonnull
  private void loadLibraryProperties(@Nonnull InputVFS vfs) throws NoSuchFileException {
    InputVFile libProp;
    try {
      libProp = vfs.getRootInputVDir().getInputVFile(JackLibrary.LIBRARY_PROPERTIES_VPATH);
    } catch (NotFileOrDirectoryException e) {
      throw new AssertionError(e);
    }

    InputStream is = null;
    try {
      is = libProp.getInputStream();
      libraryProperties.load(is);
    } catch (IOException e) {
      throw new AssertionError();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          logger.log(Level.WARNING,
              "Failed to close input stream on " + libProp.getLocation().getDescription(),
              e);
        }
      }
    }
  }
}
