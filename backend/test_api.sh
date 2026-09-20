#!/bin/bash
echo "Testing Cases API"
# POST Case
curl -s -X POST http://localhost:8080/api/cases -H "Content-Type: application/json" -d '{"caseNumber":"C-999","caseTitle":"Test Case","location":"Lab","incidentDate":"2026-09-18","status":"Active","investigator":"Admin","description":"Test desc"}'
echo -e "\nGET Cases"
curl -s http://localhost:8080/api/cases
echo -e "\n\nTesting Suspects API"
# POST Suspect
curl -s -X POST http://localhost:8080/api/suspects -H "Content-Type: application/json" -d '{"suspectId":"S-999","fullName":"Test Suspect","associatedCase":"C-999","location":"Lab","status":"Under Investigation","contactInfo":"test@test.com","notes":"None"}'
echo -e "\nGET Suspects"
curl -s http://localhost:8080/api/suspects
