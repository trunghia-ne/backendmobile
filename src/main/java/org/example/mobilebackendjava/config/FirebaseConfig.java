package org.example.mobilebackendjava.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initFirebase() throws IOException {
        FirebaseOptions options;

        String firebaseConfigEnv = System.getenv("FIREBASE_CONFIG");

        if (firebaseConfigEnv != null && !firebaseConfigEnv.isEmpty()) {
            // ✅ Ưu tiên chạy trên Render (đọc từ ENV)
            ByteArrayInputStream serviceAccount = new ByteArrayInputStream(firebaseConfigEnv.getBytes(StandardCharsets.UTF_8));
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        } else {
            // ✅ Fallback local: đọc file JSON trong src/main/resources
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
}
