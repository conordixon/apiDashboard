package apiConfigurationKeys;

public class Configuration {

    public String SANDBOX_CONSUMER_KEY() {
        return "CAvRNGVDOZ9vRJ0Nis5qd290D2WE8M12BodRyL-Z32dad64e!6103a5ca3aef458a9ac0f8ab6b79a4480000000000000000";
    }
    public String SANDBOX_KEY_ALIAS() {
        return "keyalias";
    }
    public String SANDBOX_PASSWORD() {
        return "keystorepassword";
    }
    public String SANDBOX_URL() {
        return "https://sandbox.api.mastercard.com/atms/v1/atm?PageOffset=0&PageLength=5&AddressLine1=114%20Fifth%20Avenue&AddressLine2=Apartment%201&City=New%20York%20City&CountrySubdivision=NY&PostalCode=11101&Country=USA&Latitude=38.76006576913497&Longitude=-90.74615107952418&DistanceUnit=MILE&Radius=25&SupportEMV=1&InternationalMaestroAccepted=1";
    }
    public String SANDBOX_P12_STRING() {
        return "";
    }

    public String responses() {
        return "200";
    }

    public String json(){
        return "{\"requestId\":\"hSJA-12197987-shfksdjhks-15289\",\"totalRecords\":\"1\",\"records\": [{\"pan\":\"8787628778867856\"},{\"pan\":\"22787628778867856\"}]}";
    }


    //For POST
//        String json = "{\"requestId\":\"hSJA-12197987-shfksdjhks-15289\",\"totalRecords\":\"1\",\"records\": [{\"pan\":\"8787628778867856\"},{\"pan\":\"22787628778867856\"}]}";
//        HttpPost postRequest = new HttpPost(url);
//        postRequest.addHeader("Authorization", genAuthorizationHeader(url, json, null, HttpPost.METHOD_NAME, p12Content, p12Password, keyAlias, consumerKey));
//        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
//        HttpResponse response = client.execute(postRequest);


//    switch (expression) {
//        case value1:
//        // code to be executed if
//        // expression is equal to value1
//        break;
//
//        case value2:
//        // code to be executed if
//        // expression is equal to value2
//        break;
//
//        default:
//        GET
}