package fit.biejk.minIO;

import io.minio.MinioClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import lombok.extern.slf4j.Slf4j;

/**
 * CDI producer class for creating and configuring the {@link MinioClient} instance.
 * <p>
 * This class defines a singleton-scoped producer method that allows MinIO
 * to be injected into other Quarkus beans.
 * </p>
 */
@ApplicationScoped
@Slf4j
public class MinioProducer {

    /**
     * Produces a configured {@link MinioClient} instance pointing to the local MinIO server.
     *
     * @return a configured {@link MinioClient}
     */
    @Produces
    public MinioClient minioClient() {
        log.info("Producing MinioClient for endpoint http://localhost:9000");
        return MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("iarylser", "1234567890")
                .build();
    }
}
