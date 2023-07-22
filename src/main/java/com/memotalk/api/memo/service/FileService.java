package com.memotalk.api.memo.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.memotalk.exception.InternalServerException;
import com.memotalk.exception.enumeration.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.url}")
    private String bucketUrl;
    private static final String DIR_NAME = "static/";

    private final AmazonS3Client amazonS3Client;

    public String upload(MultipartFile multipartFile) {
        String fileName = generateFileName(multipartFile);
        ObjectMetadata metadata = createMetadata(multipartFile);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return generateFileUrl(fileName);
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    private String generateFileName(MultipartFile multipartFile) {
        return DIR_NAME + UUID.randomUUID() + multipartFile.getOriginalFilename();
    }

    private String generateFileUrl(String fileName) {
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private ObjectMetadata createMetadata(MultipartFile multipartFile) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        return metadata;
    }

    // delete s3에 올려진 사진
    public void fileDelete(String imageUrl) {
        imageUrl = imageUrl.substring(bucketUrl.length());
        try {
            imageUrl = URLDecoder.decode(imageUrl, StandardCharsets.UTF_8);
            amazonS3Client.deleteObject(bucket, imageUrl);
        } catch (AmazonServiceException e) {
            throw new InternalServerException(ErrorCode.FILE_DELETE_FAIL);
        }
    }
}