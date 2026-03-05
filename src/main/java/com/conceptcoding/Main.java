package com.conceptcoding;

public class Main {

    public static void main(String[] args) {
        String str = "{\r\n    \"data\": {\r\n        \"client\": {\r\n            \"disbursements\": {\r\n                \"node\": {\r\n                    \"__typename\": \"Disbursement\",\r\n                    \"amount\": {\r\n                        \"currency\": \"ZAR\",\r\n                        \"quantity\": \"34\"\r\n                    },\r\n                    \"bankBeneficiary\": {\r\n                        \"__typename\": \"DisbursementBankBeneficiary\",\r\n                        \"accountHolder\": \"HHFHFHFF JJJGHG\",\r\n                        \"accountNumber\": \"147197171\",\r\n                        \"accountType\": \"unknown\",\r\n                        \"bankId\": \"Absa\"\r\n                    },\r\n                    \"beneficiaryReference\": \"147227189\",\r\n                    \"created\": \"2025-06-09T06:10:45.011Z\",\r\n                    \"id\": \"ZGlzYnVyc2VtZW50L2RlOTY2MGVjLTE0YTMtNDU3MC1hYzAwLTY0YTM5NTE2Zjk1MA==\",\r\n                    \"linkedAccountId\": null,\r\n                    \"nonce\": \"147227189\",\r\n                    \"status\": {\r\n                        \"__typename\": \"DisbursementCompleted\",\r\n                        \"date\": \"2025-06-09T06:10:55.296Z\",\r\n                        \"expectedSettlement\": \"2025-06-11T06:10:55.296Z\"\r\n                    }\r\n                }\r\n            }\r\n        }\r\n    },\r\n    \"type\": \"disbursement\"\r\n}";
        System.out.println(str);
    }
}
