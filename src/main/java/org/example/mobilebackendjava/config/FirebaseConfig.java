package org.example.mobilebackendjava.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initFirebase() throws IOException {
        FirebaseOptions options;

        // Cách đơn giản: Nếu có biến FIREBASE_CONFIG => dùng, còn lại thì dùng local file
        String firebaseConfigEnv = System.getenv("FIREBASE_CONFIG");

        if (firebaseConfigEnv != null && !firebaseConfigEnv.isEmpty()) {
            // ✅ Dùng bản base64 từ biến môi trường (Render)
            byte[] decoded = Base64.getDecoder().decode(firebaseConfigEnv);
            ByteArrayInputStream serviceAccount = new ByteArrayInputStream(decoded);
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        } else {
            // ✅ Nếu không có FIREBASE_CONFIG => chạy local bằng file
            InputStream serviceAccount = new FileInputStream("src/main/resources/movieapp-f0c63-firebase-adminsdk-fbsvc-694814d465.json");
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        }

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore getFirestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }
}
