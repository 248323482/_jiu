package com.jiu.swagger2;

import springfox.documentation.spring.web.paths.RelativePathProvider;

import javax.servlet.ServletContext;

/**
 * This is a Description
 *

 */
public class ExtRelativePathProvider extends RelativePathProvider {
    private String basePath;

    public ExtRelativePathProvider(ServletContext servletContext, String basePath) {
        super(servletContext);
        this.basePath = basePath;
    }

    @Override
    public String getApplicationBasePath() {
        String applicationPath = super.applicationPath();
        if (ROOT.equals(applicationPath)) {
            applicationPath = "";
        }
        return basePath + applicationPath;
    }
}
