package com.printit.backend.features.staff.orders;

import org.springframework.core.io.Resource;

public class StaffOrderDownloadFile {

    private final Resource resource;
    private final String contentType;
    private final String contentDisposition;
    private final String redirectUrl;

    public StaffOrderDownloadFile(
            Resource resource,
            String contentType,
            String contentDisposition
    ) {
        this.resource = resource;
        this.contentType = contentType;
        this.contentDisposition = contentDisposition;
        this.redirectUrl = null;
    }

    public StaffOrderDownloadFile(String redirectUrl) {
        this.resource = null;
        this.contentType = null;
        this.contentDisposition = null;
        this.redirectUrl = redirectUrl;
    }

    public Resource getResource() {
        return resource;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public boolean isRedirect() {
        return redirectUrl != null && !redirectUrl.isBlank();
    }
}
