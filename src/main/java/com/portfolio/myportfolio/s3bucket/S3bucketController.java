package com.portfolio.myportfolio.s3bucket;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import com.portfolio.myportfolio.api.Api;
import com.portfolio.myportfolio.aws.S3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/images")
public class S3bucketController {

    @Autowired
    private S3 s3_images;

    private static AtomicInteger runCount = new AtomicInteger(0);

    static final String bucket = "richard.bucket1";//your bucket name


    //get all images available in s3 bucket
    @GetMapping("/get_all_images")
    public ResponseEntity<List<S3bucket>> getAllImages() {

        List<S3ObjectSummary> objects = s3_images.get_objects(bucket);
        System.out.println(objects);
        List<S3bucket> allImages = objects
                .stream()
                .map(x -> {
                    String url = s3_images.getAssignedURL(bucket,x.getKey());
                    S3bucket s3Images = new S3bucket();
                    s3Images.setUrl(url);
                    s3Images.setId(runCount.incrementAndGet());

                    return s3Images;
                })
                .collect(Collectors.toList());

        Api apiResponse = new Api(HttpStatus.OK,"Found All Images",allImages);

        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

    // return a specific image in bucket
    @GetMapping("/get_image")
    @ResponseBody
    public ResponseEntity getImage(@RequestParam String key){
        String objectKey = s3_images.getObject(bucket,key);
        List<S3bucket> image = new ArrayList<>();
        if(objectKey != null){

            String url = s3_images.getAssignedURL(bucket,objectKey);
            S3bucket s3Image = new S3bucket();
            s3Image.setUrl(url);
            s3Image.setId(runCount.incrementAndGet());
            image.add(s3Image);
        }
        Api apiResponse = new Api(HttpStatus.OK,"Found Image",image);

        return new ResponseEntity(apiResponse, HttpStatus.OK);
    }

}
