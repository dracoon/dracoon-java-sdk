package com.dracoon.sdk.internal.service;

import java.net.URL;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.filter.GetNodesFilters;
import com.dracoon.sdk.filter.NodeTypeFilter;
import com.dracoon.sdk.filter.SearchNodesFilters;
import com.dracoon.sdk.model.CopyNodesRequest;
import com.dracoon.sdk.model.CreateFolderRequest;
import com.dracoon.sdk.model.CreateRoomRequest;
import com.dracoon.sdk.model.DeleteNodesRequest;
import com.dracoon.sdk.model.MoveNodesRequest;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NodesServiceTest extends BaseServiceTest {

    private NodesService mServ;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mServ = new NodesService(mServiceLocator, mServiceDependencies);
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

    @SuppressWarnings("unused")
    private abstract class BaseGetNodesTests extends BaseNodesTests<NodeList> {

        protected BaseGetNodesTests() {
            super(NodeList.class, "/nodes/get_nodes/");
        }

        protected void executeTestError(String responseFilename, NodesTest<NodeList> test) {
            executeTestError(responseFilename, mDracoonErrorParser::parseNodesQueryError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND, test);
        }

        protected abstract NodeList getNodes() throws Exception;

    }

    @Nested
    class GetNodesTests extends BaseGetNodesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_nodes_request.json", "get_nodes_response.json",
                    this::getNodes);
        }

        @Test
        void testNoDataCorrect() throws Exception {
            executeTestDataCorrect("get_nodes_empty_response.json", "nodes_empty.json",
                    this::getNodes);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_nodes_response.json", "nodes.json",
                    this::getNodes);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::getNodes);
        }

        @Override
        protected NodeList getNodes() throws Exception {
            return mServ.getNodes(1L);
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
                    this::getNodes);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_nodes_with_filter_response.json", "nodes_filtered.json",
                    this::getNodes);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::getNodes);
        }

        @Override
        protected NodeList getNodes() throws Exception {
            return mServ.getNodes(2L, mFilters);
        }

    }

    @Nested
    class GetNodesPagedTests extends BaseGetNodesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_nodes_paged_request.json",
                    "get_nodes_paged_response.json",
                    this::getNodes);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_nodes_paged_response.json", "nodes_paged.json",
                    this::getNodes);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::getNodes);
        }

        @Override
        protected NodeList getNodes() throws Exception {
            return mServ.getNodes(3L, 1L, 2L);
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
                    this::getNodes);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_nodes_paged_with_filter_response.json",
                    "nodes_paged_filtered.json",
                    this::getNodes);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::getNodes);
        }

        @Override
        protected NodeList getNodes() throws Exception {
            return mServ.getNodes(4L, mFilters, 1L, 2L);
        }

    }

    // --- Get node tests ---

    @SuppressWarnings("unused")
    private abstract class BaseGetNodeTests extends BaseNodesTests<Node> {

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

        protected abstract Node getNode() throws Exception;

    }

    @Nested
    class GetNodeTests extends BaseGetNodeTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_node_request.json", "get_node_response.json",
                    this::getNode);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_node_response.json", "node.json",
                    this::getNode);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::getNode);
        }

        @Override
        protected Node getNode() throws Exception {
            return mServ.getNode(4L);
        }

    }

    @Nested
    class GetNodeByPathTests extends BaseGetNodeTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_node_by_path_request.json",
                    "get_node_by_path_response.json",
                    this::getNode);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_node_by_path_response.json", "node.json",
                    this::getNode);
        }

        @Test
        void testNoDataError() {
            executeTestNoDataError("get_node_by_path_empty_response.json",
                    this::getNode);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::getNode);
        }

        @Override
        protected Node getNode() throws Exception {
            return mServ.getNode("/test/test-file.jpg");
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
                    () -> mServ.createRoom(mCreateRoomRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("create_room_response.json", "node.json",
                    () -> mServ.createRoom(mCreateRoomRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseRoomCreateError,
                    DracoonApiCode.SERVER_TARGET_ROOM_NOT_FOUND,
                    () -> mServ.createRoom(mCreateRoomRequest));
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
                    () -> mServ.updateRoom(mUpdateRoomRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("update_room_response.json", "node.json",
                    () -> mServ.updateRoom(mUpdateRoomRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseRoomUpdateError,
                    DracoonApiCode.SERVER_ROOM_NOT_FOUND,
                    () -> mServ.updateRoom(mUpdateRoomRequest));
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
                    () -> mServ.updateRoomConfig(mUpdateRoomConfigRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("update_room_config_response.json", "node.json",
                    () -> mServ.updateRoomConfig(mUpdateRoomConfigRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseRoomUpdateError,
                    DracoonApiCode.SERVER_ROOM_NOT_FOUND,
                    () -> mServ.updateRoomConfig(mUpdateRoomConfigRequest));
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
                    () -> mServ.createFolder(mCreateFolderRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("create_folder_response.json", "node.json",
                    () -> mServ.createFolder(mCreateFolderRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseFolderCreateError,
                    DracoonApiCode.SERVER_TARGET_NODE_NOT_FOUND,
                    () -> mServ.createFolder(mCreateFolderRequest));
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
                    () -> mServ.updateFolder(mUpdateFolderRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("update_folder_response.json", "node.json",
                    () -> mServ.updateFolder(mUpdateFolderRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseFolderUpdateError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND,
                    () -> mServ.updateFolder(mUpdateFolderRequest));
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
                    () -> mServ.updateFile(mUpdateFileRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("update_file_response.json", "node.json",
                    () -> mServ.updateFile(mUpdateFileRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseFileUpdateError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND,
                    () -> mServ.updateFile(mUpdateFileRequest));
        }

    }

    // --- Delete nodes tests ---

    private abstract class BaseDeleteNodesTests extends BaseNodesTests<Void> {

        protected BaseDeleteNodesTests() {
            super(Void.class, "/nodes/delete_nodes/");
        }

    }

    @Nested
    class DeleteNodesTests extends BaseDeleteNodesTests {

        private DeleteNodesRequest mDeleteNodesRequest;

        @BeforeEach
        void setup() {
            mDeleteNodesRequest = readDataWithPath(DeleteNodesRequest.class,
                    "delete_nodes_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("delete_nodes_request.json", "delete_nodes_response.json",
                    this::executeDeleteNodes);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseNodesDeleteError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND,
                    this::executeDeleteNodes);
        }

        private Void executeDeleteNodes() throws Exception {
            mServ.deleteNodes(mDeleteNodesRequest);
            return null;
        }

    }

    @Nested
    class DeleteNodeTests extends BaseDeleteNodesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("delete_node_request.json", "delete_node_response.json",
                    this::executeDeleteNode);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseNodesDeleteError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND,
                    this::executeDeleteNode);
        }

        private Void executeDeleteNode() throws Exception {
            mServ.deleteNode(4L);
            return null;
        }

    }

    // --- Copy/move nodes tests ---

    private abstract class BaseCopyMoveNodesTests extends BaseNodesTests<Node> {

        protected BaseCopyMoveNodesTests() {
            super(Node.class, "/nodes/copy_move_nodes/");
        }

    }

    @Nested
    class CopyNodesTests extends BaseCopyMoveNodesTests {

        private CopyNodesRequest mCopyNodesRequest;

        @BeforeEach
        void setup() {
            mCopyNodesRequest = readDataWithPath(CopyNodesRequest.class, "copy_nodes_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("copy_nodes_request.json", "copy_nodes_response.json",
                    () -> mServ.copyNodes(mCopyNodesRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("copy_nodes_response.json", "node.json",
                    () -> mServ.copyNodes(mCopyNodesRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseNodesCopyError,
                    DracoonApiCode.SERVER_SOURCE_NODE_NOT_FOUND,
                    () -> mServ.copyNodes(mCopyNodesRequest));
        }

    }

    @Nested
    class MoveNodesTests extends BaseCopyMoveNodesTests {

        private MoveNodesRequest mMoveNodesRequest;

        @BeforeEach
        void setup() {
            mMoveNodesRequest = readDataWithPath(MoveNodesRequest.class, "move_nodes_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("move_nodes_request.json", "move_nodes_response.json",
                    () -> mServ.moveNodes(mMoveNodesRequest));
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("move_nodes_response.json", "node.json",
                    () -> mServ.moveNodes(mMoveNodesRequest));
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseNodesMoveError,
                    DracoonApiCode.SERVER_SOURCE_NODE_NOT_FOUND,
                    () -> mServ.moveNodes(mMoveNodesRequest));
        }

    }

    // --- Search nodes tests ---

    @SuppressWarnings("unused")
    private abstract class BaseSearchNodesTests extends BaseNodesTests<NodeList> {

        protected BaseSearchNodesTests() {
            super(NodeList.class, "/nodes/search_nodes/");
        }

        protected void executeTestError(String responseFilename, NodesTest<NodeList> test) {
            executeTestError(responseFilename, mDracoonErrorParser::parseNodesQueryError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND, test);
        }

        protected abstract NodeList searchNodes() throws Exception;

    }

    @Nested
    class SearchNodesTests extends BaseSearchNodesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("search_nodes_request.json", "search_nodes_response.json",
                    this::searchNodes);
        }

        @Test
        void testNoDataCorrect() throws Exception {
            executeTestDataCorrect("search_nodes_empty_response.json", "nodes_empty.json",
                    this::searchNodes);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("search_nodes_response.json", "nodes.json",
                    this::searchNodes);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::searchNodes);
        }

        @Override
        protected NodeList searchNodes() throws Exception {
            return mServ.searchNodes(1L, "test");
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
                    this::searchNodes);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("search_nodes_with_filter_response.json", "nodes_filtered.json",
                    this::searchNodes);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::searchNodes);
        }

        @Override
        protected NodeList searchNodes() throws Exception {
            return mServ.searchNodes(2L, "test", mFilters);
        }

    }

    @Nested
    class SearchNodesPagedTests extends BaseSearchNodesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("search_nodes_paged_request.json",
                    "search_nodes_paged_response.json",
                    this::searchNodes);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("search_nodes_paged_response.json", "nodes_paged.json",
                    this::searchNodes);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::searchNodes);
        }

        @Override
        protected NodeList searchNodes() throws Exception {
            return mServ.searchNodes(3L, "test", 1L, 2L);
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
                    this::searchNodes);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("search_nodes_paged_with_filter_response.json",
                    "nodes_paged_filtered.json",
                    this::searchNodes);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::searchNodes);
        }

        @Override
        protected NodeList searchNodes() throws Exception {
            return mServ.searchNodes(4L, "test", mFilters, 1L, 2L);
        }

    }

    // --- Generate missing file keys tests ---

    @SuppressWarnings("unused")
    private abstract class BaseGenerateMissingFileKeysTests {

        @Mock
        protected FileKeyGenerator mFileKeyGenerator;

        protected Long mNodeId = null;
        protected Integer mLimit = null;

        @BeforeEach
        protected void setup() {
            mServiceLocator.set(FileKeyGenerator.class, mFileKeyGenerator);

            setParams();
        }

        @Test
        void testDependencyCallsValid() throws Exception {
            when(mFileKeyGenerator.generateMissingFileKeys(any(), any()))
                    .thenReturn(true);

            executeGenerateMissingFileKeys();

            verify(mFileKeyGenerator).generateMissingFileKeys(mNodeId, mLimit);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void testReturnCorrect(boolean expectedResult) throws Exception {
            when(mFileKeyGenerator.generateMissingFileKeys(any(), any()))
                    .thenReturn(expectedResult);

            boolean result = executeGenerateMissingFileKeys();

            assertEquals(expectedResult, result);
        }

        @Test
        void testDependencyError() throws Exception {
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_USER_FILE_KEY_NOT_FOUND;
            when(mFileKeyGenerator.generateMissingFileKeys(any(), any()))
                    .thenThrow(new DracoonApiException(expectedCode));

            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    this::executeGenerateMissingFileKeys);

            assertEquals(expectedCode, thrown.getCode());
        }

        protected abstract void setParams();

        protected abstract boolean executeGenerateMissingFileKeys() throws Exception;

    }

    @Nested
    class GenerateMissingFileKeysAllFilesTests extends BaseGenerateMissingFileKeysTests {

        @Override
        protected void setParams() {
            mNodeId = 1L;
            mLimit = 5;
        }

        @Override
        protected boolean executeGenerateMissingFileKeys() throws Exception {
            return mServ.generateMissingFileKeys(mNodeId, mLimit);
        }

    }

    @Nested
    class GenerateMissingFileKeysOneFileTests extends BaseGenerateMissingFileKeysTests {

        @Override
        protected void setParams() {
            mLimit = 5;
        }

        @Override
        protected boolean executeGenerateMissingFileKeys() throws Exception {
            return mServ.generateMissingFileKeys(mLimit);
        }

    }

    // --- Favorites tests ---

    private abstract class BaseFavoriteTests extends BaseNodesTests<Void> {

        protected BaseFavoriteTests() {
            super(Void.class, "/nodes/set_favorite/");
        }

        protected void executeTestError(String responseFilename, NodesTest<Void> test) {
            executeTestError(responseFilename, mDracoonErrorParser::parseFavoriteMarkError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND, test);
        }

    }

    @Nested
    class MarkFavoriteTests extends BaseFavoriteTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("mark_favorite_request.json", "mark_favorite_response.json",
                    this::executeMarkFavorite);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::executeMarkFavorite);
        }

        private Void executeMarkFavorite() throws Exception {
            mServ.markFavorite(10L);
            return null;
        }

    }

    @Nested
    class UnMarkFavoriteTests extends BaseFavoriteTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("unmark_favorite_request.json", "unmark_favorite_response.json",
                    this::executeUnmarkFavorite);
        }

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    this::executeUnmarkFavorite);
        }

        private Void executeUnmarkFavorite() throws Exception {
            mServ.unmarkFavorite(10L);
            return null;
        }

    }

    @SuppressWarnings("unused")
    private abstract class BaseGetFavoritesTests extends BaseNodesTests<NodeList> {

        protected BaseGetFavoritesTests() {
            super(NodeList.class, "/nodes/get_favorites/");
        }

        protected void executeTestError(String responseFilename, NodesTest<NodeList> test) {
            executeTestError(responseFilename, mDracoonErrorParser::parseNodesQueryError,
                    DracoonApiCode.PRECONDITION_UNKNOWN_ERROR, test);
        }

        protected abstract NodeList getFavorites() throws Exception;

    }

    @Nested
    class GetFavoritesTests extends BaseGetFavoritesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_favorites_request.json", "get_favorites_response.json",
                    this::getFavorites);
        }

        @Test
        void testNoDataCorrect() throws Exception {
            executeTestDataCorrect("get_favorites_empty_response.json", "favorites_empty.json",
                    this::getFavorites);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_favorites_response.json", "favorites.json",
                    this::getFavorites);
        }

        @Test
        void testError() {
            executeTestError("precondition_failed_response.json",
                    this::getFavorites);
        }

        @Override
        protected NodeList getFavorites() throws Exception {
            return mServ.getFavorites();
        }

    }

    @Nested
    class GetFavoritesPagedTests extends BaseGetFavoritesTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_favorites_paged_request.json",
                    "get_favorites_paged_response.json",
                    this::getFavorites);
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_favorites_paged_response.json", "favorites_paged.json",
                    this::getFavorites);
        }

        @Test
        void testError() {
            executeTestError("precondition_failed_response.json",
                    this::getFavorites);
        }

        @Override
        protected NodeList getFavorites() throws Exception {
            return mServ.getFavorites(1L, 2L);
        }

    }

    // --- Media URL tests ---

    @Nested
    class BuildMediaUrlTests {

        @Test
        void testResultCorrect() throws Exception {
            URL expectedMediaUrl = new URL(mServerUrl.toString() + "/mediaserver/image/xxx/800x600");
            URL mediaUrl = mServ.buildMediaUrl("xxx", 800, 600);
            assertEquals(expectedMediaUrl, mediaUrl);
        }

    }

}
