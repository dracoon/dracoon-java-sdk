package com.dracoon.sdk.internal;

import java.util.Arrays;
import java.util.List;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.model.FileVirusScanInfo;
import com.dracoon.sdk.model.FileVirusScanInfoList;
import com.dracoon.sdk.model.GetFilesVirusScanInfoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DracoonNodesVirusScanTest extends DracoonRequestHandlerTest {

    private DracoonNodesImpl mDni;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mDracoonClientImpl.setApiVersion(DracoonConstants.API_MIN_VIRUS_SCANNING);

        mDni = new DracoonNodesImpl(mDracoonClientImpl);
    }

    // --- Get virus scan infos tests ---

    @Nested
    class GetNodesVirusScanInfosTests {

        private final String DATA_PATH = "/nodes/get_virus_scan_info/";

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_virus_scan_infos_response.json");

            // Execute method to test
            executeGetFilesVirusScanInfo();

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_virus_scan_infos_request.json");
        }

        @Test
        void testNoDataCorrect() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_virus_scan_infos_empty_response.json");

            // Execute method to test
            FileVirusScanInfoList data = executeGetFilesVirusScanInfo();

            // Assert data is correct
            FileVirusScanInfoList expectedData = readData(FileVirusScanInfoList.class, DATA_PATH +
                    "virus_scan_infos_empty.json");
            assertDeepEquals(expectedData, data);
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_virus_scan_infos_response.json");

            // Execute method to test
            FileVirusScanInfoList data = executeGetFilesVirusScanInfo();

            // Assert data is correct
            FileVirusScanInfoList expectedData = readData(FileVirusScanInfoList.class, DATA_PATH +
                    "virus_scan_infos.json");
            assertDeepEquals(expectedData, data);
        }

        @Test
        void testError() {
            // Mock error parsing
            mockParseError(mDracoonErrorParser::parseNodesVirusProtectionInfoGetError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND);

            // Enqueue response
            enqueueResponse(DATA_PATH + "node_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    this::executeGetFilesVirusScanInfo);

            // Assert correct error code
            assertEquals(DracoonApiCode.SERVER_NODE_NOT_FOUND, thrown.getCode());
        }

        private FileVirusScanInfoList executeGetFilesVirusScanInfo() throws Exception {
            List<Long> nodeId = Arrays.asList(1L, 2L, 3L);
            GetFilesVirusScanInfoRequest request = new GetFilesVirusScanInfoRequest.Builder(nodeId)
                    .build();
            return mDni.getFilesVirusScanInformation(request);
        }

    }

    @Nested
    class GetNodesVirusScanInfoTests {

        private final String DATA_PATH = "/nodes/get_virus_scan_info/";

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_virus_scan_info_response.json");

            // Execute method to test
            executeGetFileVirusScanInfo();

            // Assert requests are valid
            checkRequest(DATA_PATH + "get_virus_scan_info_request.json");
        }

        @Test
        void testDataCorrect() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "get_virus_scan_info_response.json");

            // Execute method to test
            FileVirusScanInfo data = executeGetFileVirusScanInfo();

            // Assert data is correct
            FileVirusScanInfo expectedData = readData(FileVirusScanInfo.class, DATA_PATH +
                    "virus_scan_info.json");
            assertDeepEquals(expectedData, data);
        }

        @Test
        void testError() {
            // Mock error parsing
            mockParseError(mDracoonErrorParser::parseNodesVirusProtectionInfoGetError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND);

            // Enqueue response
            enqueueResponse(DATA_PATH + "node_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    this::executeGetFileVirusScanInfo);

            // Assert correct error code
            assertEquals(DracoonApiCode.SERVER_NODE_NOT_FOUND, thrown.getCode());
        }

        private FileVirusScanInfo executeGetFileVirusScanInfo() throws Exception {
            return mDni.getFileVirusScanInformation(2L);
        }

    }

    @Nested
    class DeleteMaliciousFileTests {

        private final String DATA_PATH = "/nodes/delete_malicious_file/";

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "delete_malicious_file_response.json");

            // Execute method to test
            executeDeleteMaliciousFile();

            // Assert requests are valid
            checkRequest(DATA_PATH + "delete_malicious_file_request.json");
        }

        @Test
        void testError() {
            // Mock error parsing
            mockParseError(mDracoonErrorParser::parseMaliciousFileDeleteError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND);

            // Enqueue response
            enqueueResponse(DATA_PATH + "node_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    this::executeDeleteMaliciousFile);

            // Assert correct error code
            assertEquals(DracoonApiCode.SERVER_NODE_NOT_FOUND, thrown.getCode());
        }

        private void executeDeleteMaliciousFile() throws Exception {
            mDni.deleteMaliciousFile(5L);
        }

    }

}
