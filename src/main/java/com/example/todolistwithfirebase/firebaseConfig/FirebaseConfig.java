package com.example.todolistwithfirebase.firebaseConfig;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseDatabase firebaseDatabase() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        FileInputStream serviceAccount = new FileInputStream(classLoader.getResource("ServiceJson.json").getFile());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://webpage-acff8-default-rtdb.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        return FirebaseDatabase.getInstance();
    }
}
