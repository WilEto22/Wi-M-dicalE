package com.example.crudApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration pour activer le support asynchrone
 * Permet l'envoi d'emails en arrière-plan sans bloquer les requêtes
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
