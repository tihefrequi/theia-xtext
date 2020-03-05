package {service.namespace}.datasource;


import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

import {service.namespace}.auth.IApplicationUser;

public class DataSourceRuntimeContext {
  public static enum TargetAccess {
    Path, Url, InputStream
  };

  private TargetAccess accessBy;
  private Optional<String> url = Optional.empty();
  private Optional<Path> path = Optional.empty();
  private Optional<InputStream> inputStream = Optional.empty();
  private IApplicationUser currentUser;

  private DataSourceRuntimeContext() {}

  public static DataSourceRuntimeContext withPath(IApplicationUser currentUser, Path path) {
    DataSourceRuntimeContext context = new DataSourceRuntimeContext();
    context.accessBy = TargetAccess.Path;
    context.path = Optional.of(path);
    context.currentUser = currentUser;
    return context;
  }

  public static DataSourceRuntimeContext withInputStream(IApplicationUser currentUser, InputStream inputStream) {
    DataSourceRuntimeContext context = new DataSourceRuntimeContext();
    context.accessBy = TargetAccess.InputStream;
    context.inputStream = Optional.of(inputStream);
    context.currentUser = currentUser;
    return context;
  }

  public static DataSourceRuntimeContext withUrl(IApplicationUser currentUser, String url) {
    DataSourceRuntimeContext context = new DataSourceRuntimeContext();
    context.accessBy = TargetAccess.InputStream;
    context.url = Optional.of(url);
    context.currentUser = currentUser;
    return context;
  }

  public TargetAccess getAccessBy() {
    return accessBy;
  }

  public Optional<String> getUrl() {
    return url;
  }

  public Optional<Path> getPath() {
    return path;
  }

  public Optional<InputStream> getInputStream() {
    return inputStream;
  }

  public IApplicationUser getCurrentUser() {
    return currentUser;
  }

  @Override
  public String toString() {
    return "DataSourceRuntimeContext [accessBy=" + accessBy + ", url=" + url + ", path=" + path + ", inputStream=" + inputStream 
        + ", currentUser=" + currentUser + "]";
  }

}