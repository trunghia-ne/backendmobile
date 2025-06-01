package org.example.mobilebackendjava.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initFirebase() throws IOException {
        String firebaseConfigEnv = System.getenv("FIREBASE_CONFIG");
        FirebaseOptions options;

        if (firebaseConfigEnv != null && !firebaseConfigEnv.isEmpty()) {
            // In base64 string gốc
            System.out.println("FIREBASE_CONFIG (base64): " + firebaseConfigEnv);

            // Decode base64 và in ra JSON gốc
            byte[] decoded = Base64.getDecoder().decode(firebaseConfigEnv);
            String json = new String(decoded, StandardCharsets.UTF_8);
            System.out.println("FIREBASE_CONFIG (decoded JSON): " + json);
            try (ByteArrayInputStream serviceAccount = new ByteArrayInputStream(decoded)) {
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build();
            }
        } else {
            try (InputStream serviceAccount = new FileInputStream("src/main/resources/movieapp-f0c63-firebase-adminsdk-fbsvc-694814d465.json")) {
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build();
            }
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
