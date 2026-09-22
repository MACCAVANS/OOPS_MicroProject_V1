package com.forensix.backend.service;

import com.forensix.backend.entity.*;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates a factual, paragraph-style investigation narrative from real database data.
 * Never invents facts. Missing information is stated explicitly.
 */
@Service
public class RuleBasedNarrativeService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d MMMM yyyy");
    private static final DateTimeFormatter DT_FMT   = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' HH:mm");

    public String generateNarrative(InvestigationCase c,
                                     List<Suspect> suspects,
                                     List<Evidence> evidence,
                                     List<EvidenceHistory> history) {
        StringBuilder sb = new StringBuilder();

        // ── Case overview ──────────────────────────────────────────────────────
        sb.append("Case ").append(safe(c.getCaseNumber()))
          .append(" concerns \"").append(safe(c.getCaseTitle())).append("\"");

        if (notBlank(c.getLocation())) {
            sb.append(", occurring at ").append(c.getLocation());
        }
        if (c.getIncidentDate() != null) {
            sb.append(" on ").append(c.getIncidentDate().format(DATE_FMT));
        }
        sb.append(". ");

        // ── Status and investigator ────────────────────────────────────────────
        sb.append("The investigation is currently ");
        sb.append(notBlank(c.getStatus()) ? c.getStatus().toLowerCase() : "in an unspecified state");
        sb.append(". ");

        if (notBlank(c.getInvestigator())) {
            sb.append("The case is being handled by ").append(c.getInvestigator()).append(". ");
        } else {
            sb.append("No investigator has been recorded. ");
        }

        // ── Description ───────────────────────────────────────────────────────
        if (notBlank(c.getDescription())) {
            sb.append("Case description: ").append(c.getDescription().trim()).append(" ");
        }

        // ── Suspects ──────────────────────────────────────────────────────────
        if (suspects.isEmpty()) {
            sb.append("No suspects have been recorded for this case. ");
        } else {
            sb.append("A total of ").append(suspects.size()).append(" suspect(s) have been recorded");
            String names = suspects.stream()
                    .map(s -> safe(s.getFullName()) + " (" + safe(s.getSuspectId()) + ")")
                    .collect(Collectors.joining(", "));
            sb.append(": ").append(names).append(". ");

            // Highlight any active suspects
            long active = suspects.stream()
                    .filter(s -> "Active".equalsIgnoreCase(s.getStatus()) || "Suspect".equalsIgnoreCase(s.getStatus()))
                    .count();
            if (active > 0) {
                sb.append(active).append(" suspect(s) currently hold an active status. ");
            }
        }

        // ── Evidence ──────────────────────────────────────────────────────────
        if (evidence.isEmpty()) {
            sb.append("No evidence items have been recorded for this case. ");
        } else {
            String typeList = evidence.stream()
                    .map(e -> e.getType() + " (" + safe(e.getEvidenceId()) + ")")
                    .collect(Collectors.joining(", "));
            sb.append(evidence.size()).append(" evidence item(s) have been collected, including: ")
              .append(typeList).append(". ");

            // Evidence status summary
            long stored   = evidence.stream().filter(e -> "Stored".equalsIgnoreCase(e.getStatus())).count();
            long examined = evidence.stream().filter(e -> "Examined".equalsIgnoreCase(e.getStatus())).count();
            if (stored > 0 || examined > 0) {
                sb.append("Of these, ").append(examined).append(" have been examined and ")
                  .append(stored).append(" are currently stored. ");
            }
        }

        // ── Chain of custody ──────────────────────────────────────────────────
        if (!history.isEmpty()) {
            // history is already ordered desc by timestamp
            EvidenceHistory latest = history.get(0);
            String latestTs = latest.getTimestamp() != null
                    ? latest.getTimestamp().format(DT_FMT)
                    : "an unrecorded date";
            String latestHandler = notBlank(latest.getHandler()) ? latest.getHandler() : "an unrecorded handler";
            sb.append("The most recent recorded evidence activity was \"")
              .append(safe(latest.getAction())).append("\" performed on ")
              .append(latestTs).append(" by ").append(latestHandler).append(". ");
            sb.append("The chain of custody contains ").append(history.size())
              .append(" recorded event(s) in total. ");
        } else {
            sb.append("No evidence history events have been recorded for this case. ");
        }

        // ── Conclusion ────────────────────────────────────────────────────────
        if (notBlank(c.getConclusion())) {
            sb.append("The recorded conclusion is: ").append(c.getConclusion().trim());
            if (!c.getConclusion().trim().endsWith(".")) sb.append(".");
        } else {
            sb.append("No formal conclusion has been recorded for this case at this time.");
        }

        return sb.toString();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static String safe(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}
