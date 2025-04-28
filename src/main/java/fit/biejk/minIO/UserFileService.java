package fit.biejk.minIO;

import fit.biejk.entity.User;
import fit.biejk.service.UserService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class UserFileService {

    private static final String BUCKET = "user-avatars";

    @Inject
    MinioClient minioClient;

    @Inject
    UserService userService;

    /**
     * Uploads the avatar to MinIO and saves the avatar key to the user.
     */
    @Transactional
    public void uploadAvatar(Long userId, InputStream fileInputStream, String contentType) throws Exception {
        User user = userService.getById(userId);

        byte[] fileBytes = fileInputStream.readAllBytes();
        String avatarKey = "avatars/" + userId;

        ensureBucketExists();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(BUCKET)
                        .object(avatarKey)
                        .stream(new ByteArrayInputStream(fileBytes), fileBytes.length, -1)
                        .contentType(contentType)
                        .build()
        );

        user.setAvatarKey(avatarKey);
        user.persist();
    }

    /**
     * Generates a presigned URL for downloading the user's avatar.
     */
    public String getAvatarUrl(Long userId) throws Exception {
        User user = userService.getById(userId);

        if (user.getAvatarKey() == null) {
            throw new IllegalStateException("User does not have an avatar");
        }

        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(BUCKET)
                        .object(user.getAvatarKey())
                        .expiry(7, TimeUnit.DAYS)
                        .build()
        );
    }

    private void ensureBucketExists() throws Exception {
        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(BUCKET).build()
        );
        if (!found) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(BUCKET).build()
            );
        }
    }
}
