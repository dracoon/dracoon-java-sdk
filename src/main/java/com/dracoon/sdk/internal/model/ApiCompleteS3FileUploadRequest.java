package com.dracoon.sdk.internal.model;

import java.util.List;

public class ApiCompleteS3FileUploadRequest extends ApiCompleteFileUploadRequest {
    public List<ApiS3FileUploadPart> parts;
}
