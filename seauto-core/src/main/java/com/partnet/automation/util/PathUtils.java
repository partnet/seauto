/*
 * Copyright 2015 Partnet, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.partnet.automation.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class PathUtils
{

  private PathUtils()
  {
    // do nothing, only allow static access
  }

  private static final Logger LOG = LoggerFactory.getLogger(PathUtils.class);

  /**
   * Search for a specific file nested inside of a specific path
   * 
   * @param path
   *          - path that contains the file
   * @param fileName
   *          - file name of the file
   * @return - the file that was found
   * 
   * @throws IllegalStateException
   *           - if more then one file with that specific name was found.
   */
  public static File getFileInPath(final String path, final String fileName)
  {
    File dir = new File(path);

    // find the correct file
    List<File> files = (List<File>) FileUtils.listFiles(dir, FileFilterUtils.nameFileFilter(fileName), TrueFileFilter.TRUE);

    LOG.debug("Files found: {}", Arrays.asList(files));

    if (files.size() != 1) {
      throw new IllegalStateException(String.format("Searching for a file '%s' did not result in the correct number of files! Found %d, expected %d", fileName, files.size(), 1));
    }

    return files.get(0);
  }

  /**
   * Gets the path to /&lt;project_dir&gt;/src/test/resources/stories
   * @return {@link Path} /&lt;project_dir&gt;/src/test/resources/stories
   */
  public static Path getStoriesPath()
  {
    return getTestPath().appendFolders("resources", "stories");
  }

  /**
   * Gets the path to /&lt;project_dir&gt;
   * @return {@link Path} for the current project
   */
  public static Path getProjectPath()
  {
    return new PathUtils.Path().appendFolders(System.getProperty("user.dir"));
  }

  /**
   * Gets the path to /&lt;project_dir&gt;/src/test
   */
  private static Path getTestPath()
  {
    return getProjectPath().appendFolders("src", "test");
  }

  /**
   * Gets the path to /&lt;project_dir&gt;/resources/
   * @return {@link Path} for the base resources.
   */
  public static Path getBaseResourcesPath()
  {
    return getProjectPath().appendFolders("resources");
  }

  public static Path getAttachmentFiles()
  {
    return getBaseResourcesPath().appendFolders("test-data", "attachment-files");
  }

  /**
   * Class to allow Path to append folders/files
   * 
   * @author <a href="mailto:bbarker@part.net">bbarker</a>
   */
  public static class Path
  {

    private final StringBuilder path = new StringBuilder();

    private boolean pathFinalized = false;

    private Path()
    {
      // don't allow just anyone to create a path. Intended to force use of
      // PathUtils.
    }

    /**
     * Appends folders to the given {@link StringBuilder}. Each folder given is
     * appended with a file separator afterwards
     * 
     * @param folders folders to append.
     * @return {@link Path} with new folder
     */
    public Path appendFolders(String... folders)
    {
      checkEditable();

      for (String singleFolder : folders) {
        path.append(singleFolder).append(File.separator);
      }
      return this;
    }

    /**
     * Finishes the path with a file. Once a file has been added, the path is
     * locked and can not be changed.
     * @param file file to append
     *
     * @return a new path containing the appended file
     */
    public Path appendFile(String file)
    {
      checkEditable();
      path.append(file);
      // this path is no longer editable
      pathFinalized = true;
      return this;
    }

    /**
     * verifies the path is editable
     */
    private void checkEditable()
    {
      if (pathFinalized) {
        throw new IllegalArgumentException(String.format("Can not further edit this path! Current path: %s", this.toString()));
      }
    }

    @Override
    public String toString()
    {
      return path.toString();
    }
  }
}
