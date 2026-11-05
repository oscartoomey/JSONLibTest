package com.openapi.demo1;

import com.networknt.schema.*;
import com.networknt.schema.Error;
import com.networknt.schema.dialect.Dialects;
import com.networknt.schema.output.OutputUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class Demo1ApplicationTests {

    @Test
    void failPostTest() throws Exception {
        SchemaRegistry schemaRegistry = SchemaRegistry.withDefaultDialect(Dialects.getOpenApi30());

        Schema RequestSchema = schemaRegistry.getSchema(
                SchemaLocation.of("classpath:time-to-pay-v1.0.0.yaml#/components/schemas/TTPQuoteRequest"));

        Schema ResponseSchema = schemaRegistry.getSchema(
                SchemaLocation.of("classpath:time-to-pay-v1.0.0.yaml#/components/schemas/TTPQuoteResponse"));

        String input = """
                {
                  "regimeType": "SA",
                  "customerReference": "uniqRef1234",
                  "channelIdentifier": "selfService",
                  "plan": {
                    "quoteType": "duration",
                    "quoteDate": "2021-05-13",
                    "instalmentStartDate": "2021-05-13",
                    "instalmentAmount": "100",
                    "frequency": "annually",
                    "duration": "12",
                    "initialPaymentAmount": 100,
                    "initialPaymentDate": "2021-05-13",
                    "paymentPlanType": "timeToPay"
                  },
                  "customerPostCodes": [
                    { "addressPostcode": "NW9 5XW", "postcodeDate": "2021-05-13" }
                  ],
                  "debtItemCharges": [
                    {
                      "debtItemChargeId": "debtItemChrgId1",
                      "mainTrans": "1546",
                      "subTrans": "1090",
                      "originalDebtAmount": 100,
                      "interestStartDate": "2021-05-13",
                      "paymentHistory": [
                        { "paymentDate": "2021-05-13", "paymentAmount": 100 }
                      ]
                    }
                  ]
                }
                """;

        String ResponseInput = """
                {
                  "quoteReference": "quoteRef1234",
                  "customerReference": "uniqRef1234",
                  "quoteType": "duration",
                  "quoteDate": "2021-05-13",
                  "numberOfInstalments": 2,
                  "totalDebtIncInt": 210.50,
                  "interestAccrued": 5.25,
                  "planInterest": 5.25,
                  "totalInterest": 10.50,
                  "instalments": [
                    {
                      "debtItemChargeId": "debtItemChrgId1",
                      "dueDate": "2021-06-13",
                      "amountDue": 105.25,
                      "expectedPayment": 105.25,
                      "interestRate": 2.75,
                      "instalmentNumber": 1,
                      "instalmentInterestAccrued": 2.60,
                      "instalmentBalance": 105.25
                    },
                    {
                      "debtItemChargeId": "debtItemChrgId1",
                      "dueDate": "2021-07-13",
                      "amountDue": 105.25,
                      "expectedPayment": 105.25,
                      "interestRate": 2.75,
                      "instalmentNumber": 2,
                      "instalmentInterestAccrued": 2.65,
                      "instalmentBalance": 0
                    }
                  ],
                  "collections": {
                    "initialCollection": { "dueDate": "2021-05-13", "amountDue": 100 },
                    "regularCollections": [
                      { "dueDate": "2021-06-13", "amountDue": 105.25 },
                      { "dueDate": "2021-07-13", "amountDue": 105.25 }
                    ]
                  }
                }
                """;

        List<Error> RequestErrors = RequestSchema.validate(input, InputFormat.JSON, executionContext -> {
            executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true));
        });

        List<Error> ResponseErrors = ResponseSchema.validate(ResponseInput, InputFormat.JSON, executionContext -> {
            executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true));
        });

        System.out.println(RequestErrors);

        System.out.println(ResponseErrors);

        assertTrue(RequestErrors.isEmpty());
        assertTrue(ResponseErrors.isEmpty());
        }
    }
