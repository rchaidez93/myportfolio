package com.portfolio.myportfolio.Controller;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import com.portfolio.myportfolio.AWS.S3;
import com.portfolio.myportfolio.Model.S3Images;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/images")
public class MyImages {

    @Autowired private S3 s3_images;

    private static AtomicInteger runCount = new AtomicInteger(0);


    //get all images available in s3 bucket
    @GetMapping("/get_all_images")
    public String getAllImages() {

        String bucket = "";//your bucket name


        List<S3ObjectSummary> objects = s3_images.get_objects(bucket);

        List<S3Images> allImages = objects
                .stream()
                .map(x -> {
                    String url = s3_images.getAssignedURL(bucket,x.getKey());
                    S3Images s3Images = new S3Images();
                    s3Images.setUrl(url);
                    s3Images.setId(runCount.incrementAndGet());

                    return s3Images;
                })
                .collect(Collectors.toList());

        Gson gson = new Gson();
        String allS3Images = gson.toJson(allImages);



        return allS3Images;
    }

}
