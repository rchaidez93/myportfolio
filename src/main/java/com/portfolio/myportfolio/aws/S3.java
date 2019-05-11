package com.portfolio.myportfolio.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.List;

@Component
public class S3 {
    final Regions region = Regions.US_EAST_2;

    final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();

    // List all objects in specified bucket
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

    // get the specified object in bucket
    public String getObject(String bucket, String key){

        String object = null;
        try {

            boolean exists = s3.doesObjectExist(bucket,key);
            if(exists){
                S3Object result = s3.getObject(new GetObjectRequest(bucket,key));
                object = result.getKey();
            }
        }catch (AmazonServiceException e){
            System.out.println(e.getErrorMessage());
            System.exit(1);
        }

        return object;
    }

    // delete specified object in bucket
    public boolean deleteObject(String bucket, String key){

        try {
            s3.deleteObject(bucket,key);
        }catch(AmazonServiceException e){
            System.out.println(e.getErrorMessage());
            System.exit(1);
        }

        return true;
    }

    public String putObject(String bucket, String key, File file, String contentType){

        try {
            PutObjectRequest request = new PutObjectRequest(bucket,key,file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            request.setMetadata(metadata);
            s3.putObject(request);

        }catch (AmazonServiceException e){
            System.out.println(e.getErrorMessage());
        }catch (SdkClientException er){
            er.printStackTrace();
        }

        return "";
    }


    // generate the aws assigned url
    public String getAssignedURL(String bucket, String obj){

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, obj);

        URL url = s3.getUrl(bucket,obj);

        return url.toString();
    }

}
