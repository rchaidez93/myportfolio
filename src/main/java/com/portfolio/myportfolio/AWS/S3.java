package com.portfolio.myportfolio.AWS;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;

@Component
public class S3 {
    final Regions region = Regions.US_EAST_2;

    final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();

    public List<S3ObjectSummary> get_objects(String bucket) {

        List<S3ObjectSummary> objects = null;

        try{
            ListObjectsV2Result result = s3.listObjectsV2(bucket);
             objects = result.getObjectSummaries();

            return objects;
        }catch(AmazonServiceException e){
            System.out.println(e.getErrorMessage());
            System.exit(1);
        }

        return objects;
    }

    public String getAssignedURL(String bucket, String obj){

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, obj);

        URL url = s3.getUrl(bucket,obj);

        return url.toString();
    }

}
