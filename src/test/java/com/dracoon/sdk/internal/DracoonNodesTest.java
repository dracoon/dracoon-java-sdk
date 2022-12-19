package com.dracoon.sdk.internal;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.filter.GetNodesFilters;
import com.dracoon.sdk.filter.NodeTypeFilter;
import com.dracoon.sdk.filter.SearchNodesFilters;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.Node;
import com.dracoon.sdk.model.NodeList;
import com.dracoon.sdk.model.NodeType;
import com.dracoon.sdk.model.UpdateFileRequest;
import com.dracoon.sdk.model.UpdateFolderRequest;
import com.dracoon.sdk.model.UpdateRoomConfigRequest;
import com.dracoon.sdk.model.UpdateRoomRequest;
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

    private interface NodesTest<T> {
        T execute() throws Exception;
    }

    private abstract class BaseNodesTests<T> {

        private final Class<T> mDataClass;
        private final String mDataPath;

        protected BaseNodesTests(Class<T> dataClass, String dataPath) {
            mDataClass = dataClass;
            mDataPath = dataPath;
        }

        protected void executeTestRequestsValid(String requestFilename, String responseFilename,
                NodesTest<T> test) throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            test.execute();

            // Assert requests are valid
            checkRequest(mDataPath + requestFilename);
        }

        protected void executeTestDataCorrect(String responseFilename, String dataFilename,
                NodesTest<T> test) throws Exception {
            // Enqueue responses
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            T data = test.execute();

            // Assert data is correct
            T expectedData = readData(mDataClass, mDataPath + dataFilename);
            assertDeepEquals(expectedData, data);
        }

        protected void executeTestNoDataError(String responseFilename, DracoonApiCode expectedCode,
                NodesTest<T> test) {
            // Enqueue response
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, test::execute);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        protected void executeTestError(String responseFilename, ErrorParserFunction errorParserFunc,
                DracoonApiCode expectedCode, NodesTest<T> test) {
            // Mock error parsing
            mockParseError(errorParserFunc, expectedCode);

            // Enqueue response
            enqueueResponse(mDataPath + responseFilename);

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class, test::execute);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        protected <DT> DT readDataWithPath(Class<? extends DT> clazz, String filename) {
            return readData(clazz, mDataPath + filename);
        }

    }

    // --- Get nodes tests ---

    private abstract class BaseGetNodesTests extends BaseNodesTests<NodeList> {

        protected BaseGetNodesTests() {
            super(NodeList.class, "/nodes/get_nodes/");
        }

        protected void executeTestError(String responseFilename, NodesTest<NodeList> test) {
            executeTestError(responseFilename, mDracoonErrorParser::parseNodesQueryError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND, test);
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

    abstract class BaseGetNodeTests extends BaseNodesTests<Node> {

        protected BaseGetNodeTests() {
            super(Node.class, "/nodes/get_node/");
        }

        protected void executeTestNoDataError(String responseFilename, NodesTest<Node> test) {
            executeTestNoDataError(responseFilename, DracoonApiCode.SERVER_NODE_NOT_FOUND, test);
        }

        protected void executeTestError(String responseFilename, NodesTest<Node> test) {
            executeTestError(responseFilename, mDracoonErrorParser::parseNodesQueryError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND, test);
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
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_node_by_path_response.json", "node.json",
                    () -> mDni.getNode(NODE_PATH));
        }

        @Test
        void testNoDataError() {
            executeTestNoDataError("get_node_by_path_empty_response.json",
                    () -> mDni.getNode(NODE_PATH));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    () -> mDni.getNode(NODE_PATH));
        }

    }

    // --- Room/folder/file create/update tests ---

    private abstract class BaseRoomTests extends BaseNodesTests<Node> {

        protected BaseRoomTests() {
            super(Node.class, "/nodes/create_update_room/");
        }

    }

    @Nested
    class CreateRoomTests extends BaseRoomTests {

        private CreateRoomRequest mCreateRoomRequest;

        @BeforeEach
        void setup() {
            mCreateRoomRequest = readDataWithPath(CreateRoomRequest.class,
                    "create_room_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("create_room_request.json", "create_room_response.json",
                    () -> mDni.createRoom(mCreateRoomRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("create_room_response.json", "node.json",
                    () -> mDni.createRoom(mCreateRoomRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseRoomCreateError,
                    DracoonApiCode.SERVER_TARGET_ROOM_NOT_FOUND,
                    () -> mDni.createRoom(mCreateRoomRequest));
        }

    }

    @Nested
    class UpdateRoomTests extends BaseRoomTests {

        private UpdateRoomRequest mUpdateRoomRequest;

        @BeforeEach
        void setup() {
            mUpdateRoomRequest = readDataWithPath(UpdateRoomRequest.class,
                    "update_room_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("update_room_request.json", "update_room_response.json",
                    () -> mDni.updateRoom(mUpdateRoomRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("update_room_response.json", "node.json",
                    () -> mDni.updateRoom(mUpdateRoomRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseRoomUpdateError,
                    DracoonApiCode.SERVER_ROOM_NOT_FOUND,
                    () -> mDni.updateRoom(mUpdateRoomRequest));
        }

    }

    @Nested
    class UpdateRoomConfigTests extends BaseRoomTests {

        private UpdateRoomConfigRequest mUpdateRoomConfigRequest;

        @BeforeEach
        void setup() {
            mUpdateRoomConfigRequest = readDataWithPath(UpdateRoomConfigRequest.class,
                    "update_room_config_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("update_room_config_request.json",
                    "update_room_config_response.json",
                    () -> mDni.updateRoomConfig(mUpdateRoomConfigRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("update_room_config_response.json", "node.json",
                    () -> mDni.updateRoomConfig(mUpdateRoomConfigRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseRoomUpdateError,
                    DracoonApiCode.SERVER_ROOM_NOT_FOUND,
                    () -> mDni.updateRoomConfig(mUpdateRoomConfigRequest));
        }

    }

    private abstract class BaseFolderTests extends BaseNodesTests<Node> {

        protected BaseFolderTests() {
            super(Node.class, "/nodes/create_update_folder/");
        }

    }

    @Nested
    class CreateFolderTests extends BaseFolderTests {

        private CreateFolderRequest mCreateFolderRequest;

        @BeforeEach
        void setup() {
            mCreateFolderRequest = readDataWithPath(CreateFolderRequest.class,
                    "create_folder_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("create_folder_request.json", "create_folder_response.json",
                    () -> mDni.createFolder(mCreateFolderRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("create_folder_response.json", "node.json",
                    () -> mDni.createFolder(mCreateFolderRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseFolderCreateError,
                    DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND,
                    () -> mDni.createFolder(mCreateFolderRequest));
        }

    }

    @Nested
    class UpdateFolderTests extends BaseFolderTests {

        private UpdateFolderRequest mUpdateFolderRequest;

        @BeforeEach
        void setup() {
            mUpdateFolderRequest = readDataWithPath(UpdateFolderRequest.class,
                    "update_folder_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("update_folder_request.json", "update_folder_response.json",
                    () -> mDni.updateFolder(mUpdateFolderRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("update_folder_response.json", "node.json",
                    () -> mDni.updateFolder(mUpdateFolderRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseFolderUpdateError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND,
                    () -> mDni.updateFolder(mUpdateFolderRequest));
        }

    }

    @Nested
    class UpdateFileTests extends BaseNodesTests<Node> {

        UpdateFileTests() {
            super(Node.class, "/nodes/update_file/");
        }

        private UpdateFileRequest mUpdateFileRequest;

        @BeforeEach
        void setup() {
            mUpdateFileRequest = readDataWithPath(UpdateFileRequest.class,
                    "update_file_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("update_file_request.json", "update_file_response.json",
                    () -> mDni.updateFile(mUpdateFileRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("update_file_response.json", "node.json",
                    () -> mDni.updateFile(mUpdateFileRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseFileUpdateError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND,
                    () -> mDni.updateFile(mUpdateFileRequest));
        }

    }

    // --- Search nodes tests ---

    private abstract class BaseSearchNodesTests extends BaseNodesTests<NodeList> {

        protected BaseSearchNodesTests() {
            super(NodeList.class, "/nodes/search_nodes/");
        }

        protected void executeTestError(String responseFilename, NodesTest<NodeList> test) {
            executeTestError(responseFilename, mDracoonErrorParser::parseNodesQueryError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND, test);
        }

    }

    @Nested
    class SearchNodesTests extends BaseSearchNodesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("search_nodes_request.json", "search_nodes_response.json",
                    () -> mDni.searchNodes(1L, "test"));
        }

        @Test
        void testNoDataCorrect() throws Exception {
            executeTestDataCorrect("search_nodes_empty_response.json", "nodes_empty.json",
                    () -> mDni.searchNodes(1L, "test"));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("search_nodes_response.json", "nodes.json",
                    () -> mDni.searchNodes(1L, "test"));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    () -> mDni.getNodes(1L));
        }

    }

    @Nested
    class SearchNodesWithFiltersTests extends BaseSearchNodesTests {

        private final SearchNodesFilters mFilters;

        SearchNodesWithFiltersTests() {
            mFilters = new SearchNodesFilters();
            mFilters.addNodeTypeFilter(new NodeTypeFilter.Builder().eq(NodeType.FOLDER).or()
                    .eq(NodeType.FILE).build());
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("search_nodes_with_filter_request.json",
                    "search_nodes_with_filter_response.json",
                    () -> mDni.searchNodes(2L, "test", mFilters));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("search_nodes_with_filter_response.json", "nodes_filtered.json",
                    () -> mDni.searchNodes(2L, "test", mFilters));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    () -> mDni.searchNodes(2L, "test", mFilters));
        }

    }

    @Nested
    class SearchNodesPagedTests extends BaseSearchNodesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("search_nodes_paged_request.json",
                    "search_nodes_paged_response.json",
                    () -> mDni.searchNodes(3L, "test", 1L, 2L));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("search_nodes_paged_response.json", "nodes_paged.json",
                    () -> mDni.searchNodes(3L, "test", 1L, 2L));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    () -> mDni.searchNodes(3L, "test", 1L, 2L));
        }

    }

    @Nested
    class SearchNodesPagedWithFiltersTests extends BaseSearchNodesTests {

        private final SearchNodesFilters mFilters;

        SearchNodesPagedWithFiltersTests() {
            super();
            mFilters = new SearchNodesFilters();
            mFilters.addNodeTypeFilter(new NodeTypeFilter.Builder().eq(NodeType.FOLDER).or()
                    .eq(NodeType.FILE).build());
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("search_nodes_paged_with_filter_request.json",
                    "search_nodes_paged_with_filter_response.json",
                    () -> mDni.searchNodes(4L, "test", mFilters, 1L, 2L));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("search_nodes_paged_with_filter_response.json",
                    "nodes_paged_filtered.json",
                    () -> mDni.searchNodes(4L, "test", mFilters, 1L, 2L));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    () -> mDni.searchNodes(4L, "test", mFilters, 1L, 2L));
        }

    }

}
