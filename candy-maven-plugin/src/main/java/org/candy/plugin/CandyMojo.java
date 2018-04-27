package org.candy.plugin;

import com.google.gson.Gson;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.candy.upload.ReportUploader;

@Mojo(name = "candy-maven-plugin", requiresOnline = true, defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class CandyMojo extends AbstractMojo {
  private static final Gson GSON = new Gson();

  @Parameter(required = true)
  private String testReportFolder;

  @Parameter(required = true)
  private String uploadURL;

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    ReportUploader uploader = new ReportUploader(testReportFolder, uploadURL, project.getBasedir());
    getLog().info("See uploaded results at: " + uploader.uploadReport());
  }
}
