package fit.biejk.minIO;

import fit.biejk.entity.User;
import fit.biejk.service.UserService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Service for handling user file operations with MinIO,
 * including avatar uploads and presigned URL generation.
 */
@ApplicationScoped
@Slf4j
public class UserFileService {

    /**
     * Name of the MinIO bucket used to store user avatar images.
     * <p>
     * This constant defines a logical separation for storing all avatars
     * in a single bucket named <strong>"user-avatars"</strong>. The bucket is created
     * automatically if it does not exist before the first upload.
     * </p>
     */
    private static final String BUCKET = "user-avatars";


    /**
     * MinIO client for interacting with the S3-compatible storage.
     */
    @Inject
    private MinioClient minioClient;

    /**
     * Service for accessing and updating {@link User} entities.
     */
    @Inject
    private UserService userService;

    /**
     * Uploads the avatar to MinIO and saves the avatar key to the user.
     *
     * @param userId          the ID of the user
     * @param fileInputStream file data to upload
     * @param contentType     MIME type of the file (e.g. image/png)
     * @throws Exception if MinIO operation fails or user not found
     */
    @Transactional
    public void uploadAvatar(final Long userId,
                             final InputStream fileInputStream,
                             final String contentType) throws Exception {
        log.info("Uploading avatar for userId={}", userId);

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

        log.debug("Avatar uploaded and key set for userId={}", userId);
    }

    /**
     * Generates a presigned URL for downloading the user's avatar.
     *
     * @param userId ID of the user
     * @return presigned URL for accessing the avatar
     * @throws Exception if URL generation fails
     */
    public String getAvatarUrl(final Long userId) throws Exception {
        log.info("Generating avatar URL for userId={}", userId);

        User user = userService.getById(userId);
        if (user.getAvatarKey() == null) {
            log.warn("User with id={} has no avatar", userId);
            throw new IllegalStateException("User does not have an avatar");
        }

        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(BUCKET)
                        .object(user.getAvatarKey())
                        .expiry(7, TimeUnit.DAYS)
                        .build()
        );

        log.debug("Generated avatar URL for userId={}", userId);
        return url;
    }

    /**
     * Ensures that the bucket used for storing user avatars exists.
     * Creates it if it does not.
     *
     * @throws Exception if checking or creation fails
     */
    private void ensureBucketExists() throws Exception {
        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(BUCKET).build()
        );

        if (!found) {
            log.info("Bucket '{}' not found, creating...", BUCKET);
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(BUCKET).build()
            );
            log.debug("Bucket '{}' created successfully", BUCKET);
        }
    }
}
