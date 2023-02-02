package com.dracoon.sdk.internal;

import com.dracoon.sdk.error.DracoonApiCode;
import com.dracoon.sdk.error.DracoonApiException;
import com.dracoon.sdk.model.CreateNodeCommentRequest;
import com.dracoon.sdk.model.NodeComment;
import com.dracoon.sdk.model.NodeCommentList;
import com.dracoon.sdk.model.UpdateNodeCommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DracoonNodesCommentsTest extends DracoonRequestHandlerTest {

    private DracoonNodesImpl mDni;

    @BeforeEach
    protected void setup() throws Exception {
        super.setup();

        mDni = new DracoonNodesImpl(mDracoonClientImpl);
    }

    // --- Get node comments tests ---

    @SuppressWarnings("unused")
    private abstract class BaseGetNodeCommentsTests {

        private final String DATA_PATH = "/nodes/get_comments/";

        @Test
        void testError() {
            executeTestError("node_not_found_response.json",
                    mDracoonErrorParser::parseNodeCommentsQueryError,
                    DracoonApiCode.SERVER_NODE_NOT_FOUND);
        }

        protected void executeTestRequestsValid(String requestFilename, String responseFilename)
                throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + responseFilename);

            // Execute method to test
            executeGetNodeComments();

            // Assert requests are valid
            checkRequest(DATA_PATH + requestFilename);
        }

        protected void executeTestDataCorrect(String responseFilename, String dataFilename)
                throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + responseFilename);

            // Execute method to test
            NodeCommentList data = executeGetNodeComments();

            // Assert data is correct
            NodeCommentList expectedData = readData(NodeCommentList.class, DATA_PATH + dataFilename);
            assertDeepEquals(expectedData, data);
        }

        protected void executeTestError(String responseFilename, ErrorParserFunction errorParserFunc,
                DracoonApiCode expectedCode) {
            // Mock error parsing
            mockParseError(errorParserFunc, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + responseFilename);

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    this::executeGetNodeComments);

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

        protected abstract NodeCommentList executeGetNodeComments() throws Exception;

    }

    @Nested
    class GetNodeCommentsTests extends BaseGetNodeCommentsTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_comments_request.json", "get_comments_response.json");
        }

        @Test
        void testNoDataCorrect() throws Exception {
            executeTestDataCorrect("get_comments_empty_response.json", "comments_empty.json");
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_comments_response.json", "comments.json");
        }

        @Override
        protected NodeCommentList executeGetNodeComments() throws Exception {
            return mDni.getNodeComments(5L);
        }

    }

    @Nested
    class GetNodeCommentsPagedTests extends BaseGetNodeCommentsTests {

        @Test
        void testRequestsValid() throws Exception {
            executeTestRequestsValid("get_comments_paged_request.json",
                    "get_comments_paged_response.json");
        }

        @Test
        void testDataCorrect() throws Exception {
            executeTestDataCorrect("get_comments_paged_response.json", "comments_paged.json");
        }

        @Override
        protected NodeCommentList executeGetNodeComments() throws Exception {
            return mDni.getNodeComments(5L, 1L, 2L);
        }

    }

    // --- Create/update/delete node comments tests ---

    private abstract class BaseCommentTests {

        protected final String DATA_PATH = "/nodes/create_update_delete_comment/";

        protected <DT> DT readDataWithPath(Class<? extends DT> clazz, String filename) {
            return readData(clazz, DATA_PATH + filename);
        }

    }

    @Nested
    class CreateNodeCommentTests extends BaseCommentTests {

        private CreateNodeCommentRequest mCreateCommentRequest;

        @BeforeEach
        void setup() {
            mCreateCommentRequest = readDataWithPath(CreateNodeCommentRequest.class,
                    "create_comment_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "create_comment_response.json");

            // Execute method to test
            mDni.createNodeComment(mCreateCommentRequest);

            // Assert requests are valid
            checkRequest(DATA_PATH + "create_comment_request.json");
        }

        @Test
        void testDataCorrect()
                throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "create_comment_response.json");

            // Execute method to test
            NodeComment data = mDni.createNodeComment(mCreateCommentRequest);;

            // Assert data is correct
            NodeComment expectedData = readDataWithPath(NodeComment.class, "comment.json");
            assertDeepEquals(expectedData, data);
        }

        @Test
        void testError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_NODE_NOT_FOUND;
            mockParseError(mDracoonErrorParser::parseNodeCommentCreateError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "node_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    () -> mDni.createNodeComment(mCreateCommentRequest));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

    }

    @Nested
    class UpdateNodeCommentTests extends BaseCommentTests {

        private UpdateNodeCommentRequest mUpdateCommentRequest;

        @BeforeEach
        void setup() {
            mUpdateCommentRequest = readDataWithPath(UpdateNodeCommentRequest.class,
                    "update_comment_request.json");
        }

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "update_comment_response.json");

            // Execute method to test
            mDni.updateNodeComment(mUpdateCommentRequest);

            // Assert requests are valid
            checkRequest(DATA_PATH + "update_comment_request.json");
        }

        @Test
        void testDataCorrect()
                throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "update_comment_response.json");

            // Execute method to test
            NodeComment data = mDni.updateNodeComment(mUpdateCommentRequest);;

            // Assert data is correct
            NodeComment expectedData = readDataWithPath(NodeComment.class, "comment.json");
            assertDeepEquals(expectedData, data);
        }

        @Test
        void testError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_NODE_COMMENT_NOT_FOUND;
            mockParseError(mDracoonErrorParser::parseNodeCommentUpdateError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "comment_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    () -> mDni.updateNodeComment(mUpdateCommentRequest));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

    }

    @Nested
    class DeleteNodeCommentTests extends BaseCommentTests {

        @Test
        void testRequestsValid() throws Exception {
            // Enqueue responses
            enqueueResponse(DATA_PATH + "delete_comment_response.json");

            // Execute method to test
            mDni.deleteNodeComment(2L);

            // Assert requests are valid
            checkRequest(DATA_PATH + "delete_comment_request.json");
        }

        @Test
        void testError() {
            // Mock error parsing
            DracoonApiCode expectedCode = DracoonApiCode.SERVER_NODE_COMMENT_NOT_FOUND;
            mockParseError(mDracoonErrorParser::parseNodeCommentDeleteError, expectedCode);

            // Enqueue response
            enqueueResponse(DATA_PATH + "comment_not_found_response.json");

            // Execute method to test
            DracoonApiException thrown = assertThrows(DracoonApiException.class,
                    () -> mDni.deleteNodeComment(2L));

            // Assert correct error code
            assertEquals(expectedCode, thrown.getCode());
        }

    }

}
