package com.dracoon.sdk.internal;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.filter.GetNodesFilters;
import com.dracoon.sdk.filter.NodeTypeFilter;
import com.dracoon.sdk.model.NodeList;
import com.dracoon.sdk.model.NodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DracoonNodesTest extends DracoonRequestHandlerTest {

    private DracoonNodesImpl mDni;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mDni = new DracoonNodesImpl(mDracoonClientImpl);
    }

    // --- Get nodes tests ---

    interface GetNodesTest {
        NodeList execute() throws Exception;
    }

    abstract class BaseGetNodesTests {

        private final String DATA_PATH = "/nodes/get_nodes/";

        protected void executeTestRequestsValid(String requestFilename, String responseFilename,
                GetNodesTest test)
                throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + responseFilename);

            // Execute method to test
            test.execute();

            // Assert requests are valid
            checkRequest(DATA_PATH + requestFilename);
        }

        protected void executeTestDataCorrect(String responseFilename, String dataFilename,
                GetNodesTest test) throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + responseFilename);

            // Execute method to test
            NodeList nodeList = test.execute();

            // Assert data is correct
            NodeList expectedNodeList = readData(NodeList.class, DATA_PATH + dataFilename);
            assertDeepEquals(expectedNodeList, nodeList);
        }

        protected void executeTestError(GetNodesTest test) {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_NODE_NOT_FOUND;
            mockParseError(mDracoonErrorParser::parseNodesQueryError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "node_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, test::execute);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

    }

    @Nested
    class GetNodesTests extends BaseGetNodesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_nodes_request.json", "get_nodes_response.json",
                    () -> mDni.getNodes(1L));
        }

        @Test
        void testNoDataCorrect() throws Exception {
            executeTestDataCorrect("get_nodes_empty_response.json", "nodes_empty.json",
                    () -> mDni.getNodes(1L));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_nodes_response.json", "nodes.json",
                    () -> mDni.getNodes(1L));
        }

        @Test
        void testError() {
            executeTestError(() -> mDni.getNodes(1L));
        }

    }

    @Nested
    class GetNodesWithFiltersTests extends BaseGetNodesTests {

        private GetNodesFilters mFilters;

        @BeforeEach
        void setup() {
            mFilters = new GetNodesFilters();
            mFilters.addNodeTypeFilter(new NodeTypeFilter.Builder().eq(NodeType.FOLDER).or()
                    .eq(NodeType.FILE).build());
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_nodes_with_filter_request.json",
                    "get_nodes_with_filter_response.json",
                    () -> mDni.getNodes(2L, mFilters));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_nodes_with_filter_response.json", "nodes_filtered.json",
                    () -> mDni.getNodes(2L, mFilters));
        }

        @Test
        void testError() {
            executeTestError(() -> mDni.getNodes(2L, mFilters));
        }

    }

    @Nested
    class GetNodesPagedTests extends BaseGetNodesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_nodes_paged_request.json",
                    "get_nodes_paged_response.json",
                    () -> mDni.getNodes(3L, 1L, 2L));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_nodes_paged_response.json", "nodes_paged.json",
                    () -> mDni.getNodes(3L, 1L, 2L));
        }

        @Test
        void testError() {
            executeTestError(() -> mDni.getNodes(3L, 1L, 2L));
        }

    }

    @Nested
    class GetNodesPagedWithFiltersTests extends BaseGetNodesTests {

        private GetNodesFilters mFilters;

        @BeforeEach
        void setup() {
            mFilters = new GetNodesFilters();
            mFilters.addNodeTypeFilter(new NodeTypeFilter.Builder().eq(NodeType.FOLDER).or()
                    .eq(NodeType.FILE).build());
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_nodes_paged_with_filter_request.json",
                    "get_nodes_paged_with_filter_response.json",
                    () -> mDni.getNodes(4L, mFilters, 1L, 2L));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_nodes_paged_with_filter_response.json",
                    "nodes_paged_filtered.json",
                    () -> mDni.getNodes(4L, mFilters, 1L, 2L));
        }

        @Test
        void testError() {
            executeTestError(() -> mDni.getNodes(4L, mFilters, 1L, 2L));
        }

    }

}
