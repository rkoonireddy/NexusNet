package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBLikeRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.aws.s3.config.S3ClientConfiguration;
import ch.nexusnet.postmanager.exception.ResourceNotFoundException;
import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.model.PostStatus;
import ch.nexusnet.postmanager.model.PostType;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import ch.nexusnet.postmanager.model.dto.UpdatePostDTO;
import ch.nexusnet.postmanager.utils.TestDataUtils;
import net.jqwik.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Captor
    ArgumentCaptor<DynamoDBPost> dynamoDBPostCaptor;
    @Mock
    private DynamoDBPostRepository dynamoDBPostRepository;
    @Mock
    private DynamoDBCommentRepository dynamoDBCommentRepository;
    @Mock
    private DynamoDBLikeRepository dynamoDBLikeRepository;
    @Mock
    private S3ClientConfiguration s3ClientConfig;

    private PostServiceImpl postService;
    private CreatePostDTO sampleCreatePostDTO;
    private UpdatePostDTO sampleUpdatePostDTO;
    private DynamoDBPost sampleDynamoDBPost;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(dynamoDBPostRepository, dynamoDBLikeRepository, dynamoDBCommentRepository, ZoneId.of("CET"), s3ClientConfig);
        sampleCreatePostDTO = TestDataUtils.createSampleCreatePostDTO();
        sampleUpdatePostDTO = TestDataUtils.createSampleUpdatePostDTO();
        sampleDynamoDBPost = TestDataUtils.createSampleDynamoDBPost();
    }

    @Test
    void createPost_Success() {
        CreatePostDTO createPostDTO = sampleCreatePostDTO;

        DynamoDBPost mockDynamoDBPost = sampleDynamoDBPost;
        given(dynamoDBPostRepository.save(any(DynamoDBPost.class))).willReturn(mockDynamoDBPost);

        Post resultPost = postService.createPost(createPostDTO);

        verify(dynamoDBPostRepository).save(dynamoDBPostCaptor.capture());
        DynamoDBPost savedDynamoDBPost = dynamoDBPostCaptor.getValue();

        assertEquals(TestDataUtils.DEFAULT_AUTHOR_ID, savedDynamoDBPost.getAuthorId());
        assertEquals(TestDataUtils.DEFAULT_POST_TYPE.name(), savedDynamoDBPost.getType());
        assertEquals(TestDataUtils.DEFAULT_POST_STATUS.name(), savedDynamoDBPost.getStatus());
        assertEquals(TestDataUtils.DEFAULT_TITLE, savedDynamoDBPost.getTitle());
        assertEquals(TestDataUtils.DEFAULT_IMAGE, savedDynamoDBPost.getImage());
        assertEquals(TestDataUtils.DEFAULT_SHORT_DESCRIPTION, savedDynamoDBPost.getShortDescription());
        assertEquals(TestDataUtils.DEFAULT_DESCRIPTION, savedDynamoDBPost.getDescription());
        assertEquals(TestDataUtils.DEFAULT_LIKE_NUMBER, savedDynamoDBPost.getLikeNumber());
        assertEquals(TestDataUtils.DEFAULT_HASHTAGS, savedDynamoDBPost.getHashtags());
        assertNotNull(savedDynamoDBPost.getCreatedDateTime());
        assertFalse(savedDynamoDBPost.isEdited());
        assertNull(savedDynamoDBPost.getEditedDateTime());

        assertNotNull(resultPost.getId());
        assertEquals(TestDataUtils.DEFAULT_AUTHOR_ID, resultPost.getAuthorId());
        assertEquals(TestDataUtils.DEFAULT_POST_TYPE, resultPost.getType());
        assertEquals(TestDataUtils.DEFAULT_POST_STATUS, resultPost.getStatus());
        assertEquals(TestDataUtils.DEFAULT_TITLE, resultPost.getTitle());
        assertEquals(TestDataUtils.DEFAULT_IMAGE, resultPost.getImage());
        assertEquals(TestDataUtils.DEFAULT_SHORT_DESCRIPTION, resultPost.getShortDescription());
        assertEquals(TestDataUtils.DEFAULT_DESCRIPTION, resultPost.getDescription());
        assertEquals(TestDataUtils.DEFAULT_LIKE_NUMBER, resultPost.getLikeNumber());
        assertEquals(TestDataUtils.DEFAULT_HASHTAGS, resultPost.getHashtags());
        assertNotNull(resultPost.getCreatedDateTime());
        assertFalse(resultPost.isEdited());
        assertNull(resultPost.getEditedDateTime());
    }

    @Test
    void findAllPosts_Success() {
        List<DynamoDBPost> dbPosts = Arrays.asList(sampleDynamoDBPost, sampleDynamoDBPost);
        given(dynamoDBPostRepository.findDynamoDBPostsByIdStartingWith("POST")).willReturn(dbPosts);

        List<Post> posts = postService.findAllPosts();

        assertNotNull(posts);
        assertEquals(dbPosts.size(), posts.size());
    }

    @Test
    void findById_PostExists() {
        DynamoDBPost dbPost = sampleDynamoDBPost;
        given(dynamoDBPostRepository.findById(dbPost.getId())).willReturn(Optional.of(dbPost));

        Post post = postService.findById(dbPost.getId());

        assertNotNull(post);
        assertEquals(dbPost.getTitle(), post.getTitle());
    }

    @Test
    void findById_PostNotFound_ThrowsException() {
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(Optional.empty());
        String postId = sampleDynamoDBPost.getId();

        assertThrows(ResourceNotFoundException.class, () -> postService.findById(postId));
    }

    @Test
    void findByAuthorId_Success() {
        List<DynamoDBPost> dbPosts = Arrays.asList(sampleDynamoDBPost, sampleDynamoDBPost);
        given(dynamoDBPostRepository.findByAuthorId(TestDataUtils.DEFAULT_AUTHOR_ID)).willReturn(dbPosts);

        List<Post> posts = postService.findByAuthorId(TestDataUtils.DEFAULT_AUTHOR_ID);

        assertNotNull(posts);
        assertEquals(dbPosts.size(), posts.size());
    }

    @Test
    void updatePost_Success() {
        UpdatePostDTO updatePostDTO = sampleUpdatePostDTO;
        Optional<DynamoDBPost> optionalDynamoDBPost = Optional.of(sampleDynamoDBPost);
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(optionalDynamoDBPost);
        given(dynamoDBPostRepository.save(any(DynamoDBPost.class))).willReturn(sampleDynamoDBPost);

        postService.updatePost(sampleDynamoDBPost.getId(), updatePostDTO);

        verify(dynamoDBPostRepository).save(dynamoDBPostCaptor.capture());
        DynamoDBPost savedDynamoDBPost = dynamoDBPostCaptor.getValue();

        assertEquals(TestDataUtils.DEFAULT_AUTHOR_ID, savedDynamoDBPost.getAuthorId());
        assertEquals(TestDataUtils.UPDATED_POST_TYPE.name(), savedDynamoDBPost.getType());
        assertEquals(TestDataUtils.UPDATED_POST_STATUS.name(), savedDynamoDBPost.getStatus());
        assertEquals(TestDataUtils.UPDATED_TITLE, savedDynamoDBPost.getTitle());
        assertEquals(TestDataUtils.UPDATED_IMAGE, savedDynamoDBPost.getImage());
        assertEquals(TestDataUtils.UPDATED_SHORT_DESCRIPTION, savedDynamoDBPost.getShortDescription());
        assertEquals(TestDataUtils.UPDATED_DESCRIPTION, savedDynamoDBPost.getDescription());
        assertEquals(TestDataUtils.DEFAULT_LIKE_NUMBER, savedDynamoDBPost.getLikeNumber());
        assertEquals(TestDataUtils.UPDATED_HASHTAGS, savedDynamoDBPost.getHashtags());
        assertNotNull(savedDynamoDBPost.getCreatedDateTime());
        assertTrue(savedDynamoDBPost.isEdited());
        assertNotNull(savedDynamoDBPost.getEditedDateTime());
    }

    @Test
    void updatePost_NotFound_ThrowsException() {
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(Optional.empty());
        String postId = sampleDynamoDBPost.getId();

        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(postId, sampleUpdatePostDTO));
    }

    @Test
    void deletePost_PostExists() {
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(Optional.of(new DynamoDBPost()));

        assertDoesNotThrow(() -> postService.deletePost(sampleDynamoDBPost.getId()));
        verify(dynamoDBPostRepository).deleteById(sampleDynamoDBPost.getId());
    }

    @Test
    void deletePost_PostNotFound_ThrowsException() {
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(Optional.empty());
        String postId = sampleDynamoDBPost.getId();

        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(postId));
    }

    @Test
    void deletePost_NullFilesUploaded() {
        sampleDynamoDBPost.setFileUrls(null);
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(Optional.of(sampleDynamoDBPost));

        postService.deletePost(sampleDynamoDBPost.getId());

        verify(s3ClientConfig, never()).getS3client();
    }

    @Test
    void deletePost_EmptyFilesUploaded() {
        sampleDynamoDBPost.setFileUrls(List.of());
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(Optional.of(sampleDynamoDBPost));

        postService.deletePost(sampleDynamoDBPost.getId());

        verify(s3ClientConfig, never()).getS3client();
    }


    @Property
    void updatePost(@ForAll("validUpdatePostDTOs") UpdatePostDTO postDetails) {
        dynamoDBPostRepository = Mockito.mock(DynamoDBPostRepository.class);
        dynamoDBLikeRepository = Mockito.mock(DynamoDBLikeRepository.class);
        dynamoDBCommentRepository = Mockito.mock(DynamoDBCommentRepository.class);
        s3ClientConfig = Mockito.mock(S3ClientConfiguration.class);
        postService = new PostServiceImpl(dynamoDBPostRepository, dynamoDBLikeRepository, dynamoDBCommentRepository, ZoneId.of("CET"), s3ClientConfig);
        sampleCreatePostDTO = TestDataUtils.createSampleCreatePostDTO();
        sampleUpdatePostDTO = TestDataUtils.createSampleUpdatePostDTO();
        sampleDynamoDBPost = TestDataUtils.createSampleDynamoDBPost();
        String existingPostId = sampleDynamoDBPost.getId();

        when(dynamoDBPostRepository.findById(existingPostId)).thenReturn(Optional.of(TestDataUtils.createSampleDynamoDBPost()));
        when(dynamoDBPostRepository.save(any(DynamoDBPost.class))).thenReturn(sampleDynamoDBPost);

        postService.updatePost(existingPostId, postDetails);

        ArgumentCaptor<DynamoDBPost> captor = ArgumentCaptor.forClass(DynamoDBPost.class);
        verify(dynamoDBPostRepository).save(captor.capture());
        DynamoDBPost updatedPost = captor.getValue();

        assertThat(updatedPost.getId()).isNotNull();
        assertThat(updatedPost.getAuthorId()).isNotNull();
        assertThat(updatedPost.getType()).isNotNull();
        assertThat(updatedPost.getStatus()).isNotNull();
        assertThat(updatedPost.getTitle()).isNotNull();
        assertThat(updatedPost.getImage()).isNotNull();
        assertThat(updatedPost.getShortDescription()).isNotNull();
        assertThat(updatedPost.getDescription()).isNotNull();
        assertThat(updatedPost.getHashtags()).isNotNull();

        if (postDetails.getType() != null) {
            assertThat(updatedPost.getType()).isEqualTo(postDetails.getType().name());
        }
        if (postDetails.getStatus() != null) {
            assertThat(updatedPost.getStatus()).isEqualTo(postDetails.getStatus().name());
        }
        if (postDetails.getTitle() != null) {
            assertThat(updatedPost.getTitle()).isEqualTo(postDetails.getTitle());
        }
        if (postDetails.getImage() != null) {
            assertThat(updatedPost.getImage()).isEqualTo(postDetails.getImage());
        }
        if (postDetails.getShortDescription() != null) {
            assertThat(updatedPost.getShortDescription()).isEqualTo(postDetails.getShortDescription());
        }
        if (postDetails.getDescription() != null) {
            assertThat(updatedPost.getDescription()).isEqualTo(postDetails.getDescription());
        }
        if (postDetails.getHashtags() != null) {
            assertThat(updatedPost.getHashtags()).isEqualTo(postDetails.getHashtags());
        }
        if (postDetails.getType() != null || postDetails.getStatus() != null || postDetails.getTitle() != null || postDetails.getImage() != null || postDetails.getShortDescription() != null || postDetails.getDescription() != null || postDetails.getHashtags() != null) {
            assertThat(updatedPost.isEdited()).isTrue();
            assertThat(updatedPost.getEditedDateTime()).isNotNull();
        }
    }

    @Provide
    Arbitrary<UpdatePostDTO> validUpdatePostDTOs() {
        Arbitrary<PostType> postType = Arbitraries.of(PostType.class).injectNull(0.5);
        Arbitrary<PostStatus> postStatus = Arbitraries.of(PostStatus.class).injectNull(0.5);
        Arbitrary<String> title = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1).ofMaxLength(255).injectNull(0.5);
        Arbitrary<String> image = Arbitraries.strings().alpha().numeric().ofMaxLength(255).injectNull(0.5);
        Arbitrary<String> shortDescription = Arbitraries.strings().withCharRange('a', 'z').ofMaxLength(500).injectNull(0.5);
        Arbitrary<String> description = Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1).injectNull(0.5);
        Arbitrary<List<String>> hashtags = Arbitraries.strings().list().ofMinSize(0).ofMaxSize(10).injectNull(0.5);

        return Combinators.combine(postType, postStatus, title, image, shortDescription, description, hashtags)
                .as(UpdatePostDTO::new);
    }
}
