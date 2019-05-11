package com.portfolio.myportfolio.api;

import com.portfolio.myportfolio.s3bucket.S3bucket;
import lombok.Data;

import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class Api {

    private HttpStatus status;

    private String message;

    private List<S3bucket> s3Images;

    public Api(){};

    public Api(HttpStatus status, String message, List<S3bucket> s3Images) {
        this.status = status;
        this.message = message;
        this.s3Images = s3Images;
    }
}
