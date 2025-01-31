package com.globalrelay.mdscache.performance.response;

/**
 * Utility class for creating response bodies for MDS Cache
 */
public final class ResponseUtils {

    private ResponseUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Creates the sample response body for the getUserProfile endpoint.
     *
     * @param smId The smId field in the response body
     * @return The response as a {@link String}
     */
    public static String forGetUserProfile(final int smId) {
        return """
                {
                  "smId": %s,
                  "grid": 5992,
                  "organizationId": 213,
                  "firstName": "Jo",
                  "lastName": "Doe",
                  "jobTitle": "Paper Clip Analyst III",
                  "email": "jo.doe@myco.com",
                  "phoneNumbers": [
                    {
                      "number": "1604987354",
                      "type": "Home"
                    }
                  ],
                  "companyName": "Myco Holdings",
                  "companyLegalName": "Myco Holdings GmbH",
                  "version": 2,
                  "policyVersion": 1,
                  "entitlements": [
                    [
                      "Directory.Public",
                      "Directory.Company",
                      "Directory.ManageCompanyChannels",
                      "Directory.ManageBusinessChannels",
                      "Directory.CallForwardSMSPhoneNumber",
                      "Directory.AndroidText",
                      "Directory.BetaMessageApplication"
                    ]
                  ]
                }
                """.formatted(smId);
    }
    
    public static String forGetCompanyNumber(int id, String number) {
        return """
                {
                  "number": "%s",
                  "alphaCountryCode": "CA",
                  "numericCountryCode": "1",
                  "provider": "twilio",
                  "hosted": false,
                  "type": "FEDERATED",
                  "capabilities": [
                    {
                      "name": "sms"
                    }
                  ],
                  "company": {
                    "id": %d,
                    "name": "Acme Inc.",
                    "legalName": "Acme Incorporated.",
                    "lei": "549310DM41XGG84CY663"
                  },
                  "configuration": {
                    "disclaimerRequired": true,
                    "forwardingAllowed": true,
                    "mmsAllowed": true,
                    "recordingRequired": true,
                    "smsAllowed": true,
                    "voiceAllowed": true,
                    "location": "LOCAL",
                    "voicemailAllowed": true
                  },
                  "user": {
                    "id": 31551,
                    "grid": 32784,
                    "companyId": 11,
                    "firstName": "Jane",
                    "lastName": "Doe",
                    "jobTitle": "Chief Financial Officer",
                    "email": "jane.doe@example.com",
                    "endpoints": [
                      {
                        "id": "cae4c9ae-3c91-48b1-b684-4e029cf631a3",
                        "type": "CLIENT_SMS",
                        "address": "+16044846630",
                        "secondaryAddress": "+18664846630"
                      }
                    ],
                    "entitlements": [
                      "Directory.ManageBusinessChannels"
                    ],
                    "configuration": {
                      "voicemailGreetingPublicUrl": "https://mfsapi.globalrelay.com/public-files/1",
                      "voicemailGreetingPublicUrlEditable": true,
                      "voicemailAllowed": true,
                      "voicemailEditable": true,
                      "recordingPlaybackAllowed": true,
                      "recordingPlaybackEditable": true,
                      "cellularVoiceAllowed": true,
                      "cellularVoiceEditable": true,
                      "cellularAddress": "+16045259687",
                      "cellularAddressEditable": true,
                      "huntModeAddress": "+17782569874",
                      "huntModeAddressEditable": true
                    }
                  }
                }
                """.formatted(number, id);
    }

    public static String forGetUserFileCapabilities() {
        return """
                {
                  "blockedFiletypes": [
                    "exe",
                    "bat",
                    "sh"
                  ],
                  "companyId": 42,
                  "capabilities": [
                    [
                      "download",
                      "upload",
                      "RECORDING_PLAYBACK"
                    ]
                  ],
                  "maxFileBytes": 26214400
                }
                """;
    }

    public static String forResolveContact() {
        return """
                {
                  "id": "CT20A690E7D8C24B33B1A42C3F747D9ED9",
                  "type": "EXTERNAL",
                  "blocked": false,
                  "firstName": "Jamie",
                  "middleName": "James",
                  "lastName": "Doe",
                  "phoneticName": "jay-mee",
                  "nickname": "Jayjay",
                  "honorific": "Mr.",
                  "pronouns": "He/him/his",
                  "companyName": "Acme Inc.",
                  "jobTitle": "Chief Byte Sorter",
                  "department": "Quality Assurance",
                  "comment": "Best quality products",
                  "website": "www.acme.com",
                  "relationship": "Coworker",
                  "customFields": [
                    {
                      "key": "Favorite Animal",
                      "value": "Coyote"
                    }
                  ],
                  "category": "BUSINESS",
                  "sourceDirectory": "GLOBALRELAY",
                  "sourceDirectoryId": "46345",
                  "significantDate": [
                    {
                      "label": "Birthday",
                      "date": "1980-10-25"
                    }
                  ],
                  "physicalAddress": [
                    {
                      "street1": "1025 Hardwood st",
                      "street2": "Unit 276",
                      "city": "Vancouver",
                      "state": "BC",
                      "country": "Canada",
                      "postalCode": "V6E 5J9",
                      "label": "Home"
                    }
                  ],
                  "unmanagedServices": [
                    {
                      "type": "PHONE_NUMBER",
                      "address": "7784328765",
                      "normalizedAddress": "+17784328765",
                      "label": "Work",
                      "url": "www.jdoe.com"
                    }
                  ],
                  "tags": [
                    2,
                    3
                  ],
                  "lastUpdated": "2020-12-01T20:00:12.414Z",
                  "version": 42,
                  "avatarVersion": 21,
                  "endpoints": [
                    {
                      "id": "cae4c9ae-3c91-48b1-b684-4e029cf631a3",
                      "type": "SMS",
                      "address": "+16044846630",
                      "secondaryAddress": "+18664846630"
                    }
                  ]
                }
                """;
    }
    
    public static String forViewDisclaimer() {
        return """
                {
                  "disclaimer": "All messages are archived to conform to requlatory requirements."
                }
                """;
    }

    public static String forGetUser(String id) {
        return """
                {
                  "id": "%s",
                  "grid": 26489,
                  "companyId": 11,
                  "firstName": "Edward",
                  "lastName": "Doe",
                  "jobTitle": "Chief Information Officer",
                  "email": "ted.doe@example.com",
                  "endpoints": [
                    {
                      "id": "cae4c9ae-3c91-48b1-b684-4e029cf631a3",
                      "type": "CLIENT_SMS",
                      "address": "+16044846630",
                      "secondaryAddress": "+18664846630",
                      "provider": "twilio",
                      "capabilities": [
                        {
                          "name": "sms"
                        }
                      ],
                      "createdAt": 1612378792810
                    }
                  ],
                  "entitlements": [
                    "Directory.CallForwardSMSPhoneNumber"
                  ],
                  "configuration": {
                    "voicemailGreetingPublicUrl": "https://mfsapi.globalrelay.com/public-files/310ab5b2-271e-4fa1",
                    "voicemailGreetingPublicUrlEditable": true,
                    "voicemailAllowed": true,
                    "voicemailEditable": true,
                    "recordingPlaybackAllowed": true,
                    "recordingPlaybackEditable": true,
                    "cellularVoiceAllowed": true,
                    "cellularVoiceEditable": true,
                    "cellularAddress": "+16045259687",
                    "cellularAddressEditable": true,
                    "huntModeAddress": "+17782569874",
                    "huntModeAddressEditable": true
                  }
                }
                """.formatted(id);
    }
}
