package com.forensix.backend.service;

import com.forensix.backend.entity.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Stub AI narrative service.
 * When forensix.ai.api-key is configured, this is where the HTTP call to
 * the AI provider would be made. Currently throws AIUnavailableException
 * so that NarrativeReportService falls back to the rule-based generator.
 *
 * To wire a real provider: inject a RestTemplate/WebClient here, build the
 * prompt from the supplied data, call the configured API endpoint, and return
 * the text from the response.
 */
@Service
public class AIReportService {

    @Value("${forensix.ai.api-key:}")
    private String apiKey;

    @Value("${forensix.ai.provider:}")
    private String provider;

    @Value("${forensix.ai.model:gpt-4o-mini}")
    private String model;

    /**
     * Attempts to generate a narrative via the configured AI provider.
     *
     * @throws AIUnavailableException if no API key is configured
     * @throws RuntimeException for any other failure (network, quota, etc.)
     */
    public String generateNarrative(InvestigationCase c,
                                     List<Suspect> suspects,
                                     List<Evidence> evidence,
                                     List<EvidenceHistory> history) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new AIUnavailableException("No AI API key configured (set FORENSIX_AI_API_KEY)");
        }
        if (provider == null || provider.isBlank()) {
            throw new AIUnavailableException("No AI provider configured (set FORENSIX_AI_PROVIDER)");
        }

        // ── Future implementation note ─────────────────────────────────────
        // 1. Build a prompt using only the data passed in (never external data).
        // 2. Add the system instruction:
        //    "You are generating a factual investigation summary from recorded
        //     case-management data. Do not invent facts. Do not infer unsupported
        //     guilt or innocence. Do not create evidence that does not exist.
        //     Clearly identify missing information. Use neutral professional language."
        // 3. Call the provider endpoint (e.g. POST https://api.openai.com/v1/chat/completions).
        // 4. Extract and return the text content of the response.
        // ───────────────────────────────────────────────────────────────────

        // Placeholder until a provider is wired up
        throw new AIUnavailableException("AI provider '" + provider + "' is configured but not yet implemented");
    }

    /** Checked-by-convention exception so callers can catch it specifically. */
    public static class AIUnavailableException extends RuntimeException {
        public AIUnavailableException(String message) { super(message); }
    }
}
