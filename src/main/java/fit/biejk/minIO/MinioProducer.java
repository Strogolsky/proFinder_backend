//package fit.biejk.minIO;
//
//import io.minio.MinioClient;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.enterprise.inject.Produces;
//import lombok.extern.slf4j.Slf4j;
//import org.eclipse.microprofile.config.inject.ConfigProperty;
//
///**
// * CDI producer class for creating and configuring the {@link MinioClient} instance.
// * <p>
// * This class defines a singleton-scoped producer method that allows MinIO
// * to be injected into other Quarkus beans.
// * </p>
// */
//@ApplicationScoped
//@Slf4j
//public class MinioProducer {
//
//    /**
//     * The endpoint URL of the MinIO server.
//     * <p>
//     * This value is injected from the application configuration property {@code minio.endpoint}.
//     * Example: {@code http://localhost:9000}
//     */
//    @ConfigProperty(name = "minio.endpoint")
//    private String endpoint;
//
//    /**
//     * The access key used for authenticating with the MinIO server.
//     * <p>
//     * This value is injected from the application configuration property {@code minio.access-key}.
//     */
//    @ConfigProperty(name = "minio.access-key")
//    private String accessKey;
//
//    /**
//     * The secret key used for authenticating with the MinIO server.
//     * <p>
//     * This value is injected from the application configuration property {@code minio.secret-key}.
//     */
//    @ConfigProperty(name = "minio.secret-key")
//    private String secretKey;
//
//
//
//    /**
//     * Produces a configured {@link MinioClient} instance pointing to the local MinIO server.
//     *
//     * @return a configured {@link MinioClient}
//     */
//    @Produces
//    public MinioClient minioClient() {
//        log.info("Producing MinioClient for endpoint {}", endpoint);
//        return MinioClient.builder()
//                .endpoint(endpoint)
//                .credentials(accessKey, secretKey)
//                .build();
//    }
//}
