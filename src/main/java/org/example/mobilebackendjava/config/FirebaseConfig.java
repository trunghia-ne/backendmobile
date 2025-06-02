package org.example.mobilebackendjava.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final String FIREBASE_CONFIG_FILE = "movieapp-f0c63-0f983a1aa75c.json";

    @Bean
    public FirebaseApp initFirebase() {
        try {
            InputStream serviceAccount = getClass()
                    .getClassLoader()
                    .getResourceAsStream(FIREBASE_CONFIG_FILE);

            if (serviceAccount == null) {
                throw new RuntimeException("Không tìm thấy file cấu hình Firebase: " + FIREBASE_CONFIG_FILE + " trong src/main/resources/");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp app = FirebaseApp.initializeApp(options);
                System.out.println("FirebaseApp đã được khởi tạo: " + app.getName());
                return app;
            } else {
                System.out.println("FirebaseApp đã được khởi tạo trước đó.");
                return FirebaseApp.getInstance();
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file credentials Firebase: " + e.getMessage(), e);
        }
    }

    @Bean
    public Firestore getFirestore(FirebaseApp firebaseApp) {
        Firestore firestore = FirestoreClient.getFirestore(firebaseApp);
        System.out.println("Firestore đã được khởi tạo.");
        return firestore;
    }
}
