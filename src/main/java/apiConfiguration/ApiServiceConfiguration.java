package apiConfiguration;

public class ApiServiceConfiguration {


    /**
     * Keys for APIs
     * and URLS for APIs
     * https://developer.mastercard.com/match/documentation/api-reference/
     * https://developer.mastercard.com/locations/documentation/api-reference/
     * */
    public static final String SANDBOX_OAUTH_KEY = "CAvRNGVDOZ9vRJ0Nis5qd290D2WE8M12BodRyL-Z32dad64e!6103a5ca3aef458a9ac0f8ab6b79a4480000000000000000";
    public static final String LOCATIONS_SANDBOX_ENDPOINT = "https://sandbox.api.mastercard.com/atms/v1/atm?PageOffset=0&PageLength=5&AddressLine1=114%20Fifth%20Avenue&AddressLine2=Apartment%201&City=New%20York%20City&CountrySubdivision=NY&PostalCode=11101&Country=USA&Latitude=38.76006576913497&Longitude=-90.74615107952418&DistanceUnit=MILE&Radius=25&SupportEMV=1&InternationalMaestroAccepted=1";
    public static final String MATCH_SANDBOX_ENDPOINT = "https://sandbox.api.mastercard.com/fraud/merchant/v3/termination-inquiry?PageLength=10&Format=JSON";

    /**
     * Common elements for accessing APIs like key and password
     * */

    public static final String SANDBOX_OAUTH_KEY_ALIAS = "keyalias";
    public static final String SANDBOX_OAUTH_KEY_PASSWORD = "keystorepassword";
    public static final String SANDBOX_P12_STRING = "";
    public static final String P12 = "/Users/conordixon/IdeaProjects/apiDashboard/src/main/resources/apiDashboardGatewayCalls-sandbox.p12";


    /**
     * Payload for POST requests
     *
     * */

    public static final String JSON = "{\n" +
            "  \"TerminationInquiryRequest\": {\n" +
            "    \"AcquirerId\": \"1996\",\n" +
            "    \"Merchant\": {\n" +
            "      \"Name\": \"THE BAIT SHOP\",\n" +
            "      \"DoingBusinessAsName\": \"BAIT R US\",\n" +
            "      \"Address\": {\n" +
            "        \"Line1\": \"42 ELM AVENUE\",\n" +
            "        \"Line2\": \"SUITE 201\",\n" +
            "        \"City\": \"DALLAS\",\n" +
            "        \"CountrySubdivision\": \"IL\",\n" +
            "        \"Province\": \"US\",\n" +
            "        \"PostalCode\": \"66579\",\n" +
            "        \"Country\": \"USA\"\n" +
            "      },\n" +
            "      \"PhoneNumber\": \"3165557625\",\n" +
            "      \"AltPhoneNumber\": \"3165557625\",\n" +
            "      \"NationalTaxId\": \"888596927\",\n" +
            "      \"CountrySubdivisionTaxId\": \"492321030\",\n" +
            "      \"ServiceProvLegal\": \"XYZ FINANCIAL SERVICE INCORPORATED\",\n" +
            "      \"ServiceProvDBA\": \"XYZ FINANCIAL SERVICE\",\n" +
            "      \"Url\": [\n" +
            "        \"www.testmerchant.com\"\n" +
            "      ],\n" +
            "      \"Principal\": [\n" +
            "        {\n" +
            "          \"FirstName\": \"DAVID\",\n" +
            "          \"MiddleInitial\": \"P\",\n" +
            "          \"LastName\": \"SMITH\",\n" +
            "          \"Address\": {\n" +
            "            \"Line1\": \"42 ELM AVENUE\",\n" +
            "            \"Line2\": \"SUITE 201\",\n" +
            "            \"City\": \"DALLAS\",\n" +
            "            \"CountrySubdivision\": \"IL\",\n" +
            "            \"Province\": \"US\",\n" +
            "            \"PostalCode\": \"66579\",\n" +
            "            \"Country\": \"USA\"\n" +
            "          },\n" +
            "          \"PhoneNumber\": \"3165557625\",\n" +
            "          \"AltPhoneNumber\": \"3165557625\",\n" +
            "          \"NationalId\": \"541022104\",\n" +
            "          \"DriversLicense\": {\n" +
            "            \"Number\": \"M15698025\",\n" +
            "            \"CountrySubdivision\": \"IL\",\n" +
            "            \"Country\": \"USA\"\n" +
            "          }\n" +
            "        }\n" +
            "      ],\n" +
            "      \"SearchCriteria\": {\n" +
            "        \"SearchAll\": \"N\",\n" +
            "        \"Region\": [\n" +
            "          \"A\"\n" +
            "        ],\n" +
            "        \"Country\": [\n" +
            "          \"USA\"\n" +
            "        ],\n" +
            "        \"MinPossibleMatchCount\": \"3\"\n" +
            "      },\n" +
            "      \"AddedOnDate\": \"10/13/2015\",\n" +
            "      \"TerminationReasonCode\": \"13\",\n" +
            "      \"AddedByAcquirerID\": \"1234\",\n" +
            "      \"UrlGroup\": [\n" +
            "        {\n" +
            "          \"ExactMatchUrls\": {\n" +
            "            \"Url\": [\n" +
            "              \"WWW.SHOESHOP.COM\"\n" +
            "            ]\n" +
            "          },\n" +
            "          \"CloseMatchUrls\": {\n" +
            "            \"Url\": [\n" +
            "              \"WWW.SHOESHOP.COM\"\n" +
            "            ]\n" +
            "          },\n" +
            "          \"NoMatchUrls\": {\n" +
            "            \"Url\": [\n" +
            "              \"WWW.SHOESHOP.COM\"\n" +
            "            ]\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  }\n" +
            "}";

}