package com.dracoon.sdk.internal;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.filter.GetNodesFilters;
import com.dracoon.sdk.filter.NodeTypeFilter;
import com.dracoon.sdk.model.Node;
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

    private interface QueryNodesTest<T> {
        T execute() throws Exception;
    }

    private abstract class BaseQueryNodesTests<T> {

        private final Class<T> mDataClass;
        private final String mDataPath;

        protected BaseQueryNodesTests(Class<T> dataClass, String dataPath) {
            mDataClass = dataClass;
            mDataPath = dataPath;
        }

        protected void executeTestRequestsValid(String requestFilename, String responseFilename,
                QueryNodesTest<T> test) throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            test.execute();

            // Assert requests are valid
            checkRequest(mDataPath + requestFilename);
        }

        protected void executeTestNoData(String responseFilename, QueryNodesTest<T> test) {
            // Enqueue response
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, test::execute);

            // Assert correct error code
            assertEquals(DracoonApiCode.SERVER_NODE_NOT_FOUND, thrown.getCode());
        }

        protected void executeTestDataCorrect(String responseFilename, String dataFilename,
                QueryNodesTest<T> test) throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            T data = test.execute();

            // Assert data is correct
            T expectedData = readData(mDataClass, mDataPath + dataFilename);
            assertDeepEquals(expectedData, data);
        }

        protected void executeTestError(String responseFilename, QueryNodesTest<T> test) {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_NODE_NOT_FOUND;
            mockParseError(mDracoonErrorParser::parseNodesQueryError, expectedCode);

            // Enqueue response
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, test::execute);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

    }

    // --- Get nodes tests ---

    private abstract class BaseGetNodesTests extends BaseQueryNodesTests<NodeList> {

        protected BaseGetNodesTests() {
            super(NodeList.class, "/nodes/get_nodes/");
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
            executeTestError("node_not_found_response.json",
                    () -> mDni.getNodes(1L));
        }

    }

    @Nested
    class GetNodesWithFiltersTests extends BaseGetNodesTests {

        private final GetNodesFilters mFilters;

        GetNodesWithFiltersTests() {
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
            executeTestError("node_not_found_response.json",
                    () -> mDni.getNodes(2L, mFilters));
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
            executeTestError("node_not_found_response.json",
                    () -> mDni.getNodes(3L, 1L, 2L));
        }

    }

    @Nested
    class GetNodesPagedWithFiltersTests extends BaseGetNodesTests {

        private final GetNodesFilters mFilters;

        GetNodesPagedWithFiltersTests() {
            super();
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
            executeTestError("node_not_found_response.json",
                    () -> mDni.getNodes(4L, mFilters, 1L, 2L));
        }

    }

    // --- Get node tests ---

    abstract class BaseGetNodeTests extends BaseQueryNodesTests<Node> {

        protected BaseGetNodeTests() {
            super(Node.class, "/nodes/get_node/");
        }

    }

    @Nested
    class GetNodeTests extends BaseGetNodeTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_node_request.json", "get_node_response.json",
                    () -> mDni.getNode(4L));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_node_response.json", "node.json",
                    () -> mDni.getNode(4L));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    () -> mDni.getNode(4L));
        }

    }

    @Nested
    class GetNodeByPathTests extends BaseGetNodeTests {

        private final String NODE_PATH = "/test/test-file.jpg";

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_node_by_path_request.json",
                    "get_node_by_path_response.json",
                    () -> mDni.getNode(NODE_PATH));
        }

        @Test
        void testNoData() {
            executeTestNoData("get_node_by_path_empty_response.json",
                    () -> mDni.getNode(NODE_PATH));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_node_by_path_response.json", "node.json",
                    () -> mDni.getNode(NODE_PATH));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    () -> mDni.getNode(NODE_PATH));
        }

    }

}
