package com.portfolio.myportfolio.s3bucket;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import com.portfolio.myportfolio.api.Api;
import com.portfolio.myportfolio.aws.S3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    //few ways of storing access keys


    //get all images available in s3 bucket
    @GetMapping("/get_all_images")
    public ResponseEntity<List<S3bucket>> getAllImages() {

        List<S3ObjectSummary> objects = s3_images.get_objects(bucket);
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
        runCount.getAndSet(0);
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

    @DeleteMapping("/delete_image")
    @ResponseBody
    public ResponseEntity deleteImage(@RequestParam String key){
        String objectKey = s3_images.getObject(bucket,key);

        if(objectKey != null){

            s3_images.deleteObject(bucket,key);

        }
        Api apiResponse = new Api();
        apiResponse.setMessage("Image deleted successfully");
        apiResponse.setStatus(HttpStatus.OK);

        return new ResponseEntity(apiResponse,HttpStatus.OK);
    }

    @PostMapping("/add_image")
    @ResponseBody
    public ResponseEntity addImage(@RequestParam String name,@RequestParam MultipartFile file) throws IOException {
        Api apiResponse = new Api();
        //make sure is it of image type file
        apiResponse.setStatus(HttpStatus.OK);

        String[] fileType = file.getContentType().split("/");

        if(!file.isEmpty() && fileType[0].equals("image")){

            byte[] bytes = file.getBytes();

            String rootPath = System.getProperty("catalina.home");

            File dir = new File(rootPath+File.separator+"tempFiles");

            if(!dir.exists()) dir.mkdir();

            File serverFile = new File(dir.getAbsolutePath()+File.separator+name);
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();

            //add to the bucket
            s3_images.putObject(bucket,file.getOriginalFilename(),serverFile,file.getContentType());

        }
        else{
            apiResponse.setMessage("File is empty");
        }

        return new ResponseEntity(apiResponse,HttpStatus.OK);
    }

}
